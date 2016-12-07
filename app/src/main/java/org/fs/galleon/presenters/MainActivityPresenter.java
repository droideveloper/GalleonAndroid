/*
 * Galleon Copyright (C) 2016 Fatih.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.fs.galleon.presenters;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.util.SparseArrayCompat;
import android.util.Log;
import android.view.MenuItem;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import org.fs.common.AbstractPresenter;
import org.fs.common.BusManager;
import org.fs.data.ValidationResult;
import org.fs.galleon.GalleonApplication;
import org.fs.galleon.R;
import org.fs.galleon.commons.SearchQueryValidator;
import org.fs.galleon.entities.Customer;
import org.fs.galleon.events.SearchFoundEvent;
import org.fs.galleon.events.TitleEvent;
import org.fs.galleon.managers.IFileManager;
import org.fs.galleon.managers.IPrefManager;
import org.fs.galleon.nets.IEndpoint;
import org.fs.galleon.nets.Response;
import org.fs.galleon.usecases.IKeepAliveUseCase;
import org.fs.galleon.usecases.ISearchCustomersUseCase;
import org.fs.galleon.usecases.KeepAliveUseCase;
import org.fs.galleon.usecases.SearchCustomersUseCase;
import org.fs.galleon.views.IMainActivityView;
import org.fs.galleon.views.PreferenceFragmentView;
import org.fs.galleon.views.SearchFragmentView;
import org.fs.galleon.views.ToolsFragmentView;
import rx.Observable;
import rx.Subscription;

import static android.support.design.widget.NavigationView.OnNavigationItemSelectedListener;

public class MainActivityPresenter extends AbstractPresenter<IMainActivityView>
    implements IMainActivityPresenter {

  private final static String KEY_SELECTED_ITEM = "main.selected.item";
  private final static String KEY_FIRST_LAUNCH  = "main.first.launch";

  private final static long QUERY_DELAY = 1000L; //1 sec

  private final static SparseArrayCompat<Fragment> itemsCache;
  static {
    itemsCache = new SparseArrayCompat<>();
    itemsCache.append(R.id.viewNavigationFindCustomers, new SearchFragmentView());
    itemsCache.append(R.id.viewNavigationTools, new ToolsFragmentView());
    itemsCache.append(R.id.viewNavigationSettings, new PreferenceFragmentView());
  }

  @IdRes
  private int     selectedNavigation;
  private boolean firstLaunch = true;

  private Subscription eventSub;

  private IPrefManager prefManager;
  private IEndpoint endpoint;
  private IFileManager fileManager;
  private ISearchCustomersUseCase usecase;
  private IKeepAliveUseCase keepAliveUseCase;


  public MainActivityPresenter(IMainActivityView view, IEndpoint endpoint, IFileManager fileManager, IPrefManager prefManager) {
    super(view);
    this.endpoint = endpoint;
    this.fileManager = fileManager;
    this.prefManager = prefManager;
  }

  @Override public void onCreate() {
    view.setUpViews();
    keepAliveUseCase = new KeepAliveUseCase.Builder()
        .endpoint(endpoint)
        .prefManager(prefManager)
        .build();
  }

  @Override public void onStart() {
    if (firstLaunch) {
      //select a navigation
      selectedNavigation = R.id.viewNavigationFindCustomers;//start of first launch
      view.setSelected(selectedNavigation);//set selected
      Fragment fragment = itemsCache.get(selectedNavigation); //get it from cache
      view.replaceAndCommit(fragment);//send fragment
      view.closeMenu();//might need to close it
      firstLaunch = false;
    }
    eventSub = BusManager.add((evt) -> {
      if (evt instanceof TitleEvent) {
        TitleEvent event = (TitleEvent) evt;
        view.setTitleText(event.titleStr());
      }
    });
    //sync task go on this
    fileManager.start();
    keepAliveUseCase.start();
  }

  @Override public void onStop() {
    if (eventSub != null) {
      BusManager.remove(eventSub);
      eventSub = null;
    }
    //sync task go on this
    fileManager.stop();
    keepAliveUseCase.stop();
  }

  @Override public void onBackPressed() {
    if (view.isMenuOpen()) {
      view.closeMenu();
    } else {
      view.finish();
    }
  }

  @Override public void restoreState(Bundle restoreState) {
    if (restoreState != null) {
      if (restoreState.containsKey(KEY_FIRST_LAUNCH)) {
        firstLaunch = restoreState.getBoolean(KEY_FIRST_LAUNCH, true);
      } else {
        firstLaunch = true;
      }
      if(restoreState.containsKey(KEY_SELECTED_ITEM)) {
        selectedNavigation = restoreState.getInt(KEY_SELECTED_ITEM, 0);
      }
    }
  }

  @Override public void storeState(Bundle storeState) {
    storeState.putBoolean(KEY_FIRST_LAUNCH, firstLaunch);
    storeState.putInt(KEY_SELECTED_ITEM, selectedNavigation);
  }

  @Override public void handleSearch(Intent intent) {
    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
      String query = intent.getStringExtra(SearchManager.QUERY);
      Observable.just(query)
          .flatMap(txt -> {
            SearchQueryValidator q = SearchQueryValidator.newInstance();
            ValidationResult<String> valid = q.validate(txt, Locale.getDefault());
            return Observable.just(valid);
          })
          .filter(ValidationResult::isValid)
          .subscribe(isValid -> {
            if (!view.isProgressVisible()) {
              view.showProgress();
            }
            //creates if not previously initialized if there is any we need previous subscription to be canceled accordingly
            createIfUsecaseNotExists(isValid.value());
            usecase.executeAsyncWidthDelay(new ISearchCustomersUseCase.Callback() {
              @Override public void onSuccess(Response<List<Customer>> response) {
                if (response.isSuccess()) {
                  //we send it to our customer
                  BusManager.send(new SearchFoundEvent(response.data()));
                } else {
                  //show error
                  log(Log.WARN,
                      String.format(Locale.ENGLISH, "ErrorCode: %d\nErrorMessage: %s\n", response.code(), response.message())
                  );
                  view.showError(
                      String.format(Locale.ENGLISH, "Error occured while executing.. '%s'", response.message())
                  );
                }
              }

              @Override public void onError(Throwable thr) {
                log(thr);
              }

              @Override public void onCompleted() {
                view.hideProgress();
              }
            }, QUERY_DELAY, TimeUnit.MILLISECONDS);
          });
    }
  }

  @Override public boolean hasMenuItemSelection(MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      if (view.isMenuOpen()) {
        view.closeMenu();
      } else {
        view.openMenu();
      }
      return true;
    }
    return false;
  }

  @Override public OnNavigationItemSelectedListener provideNavigationListener() {
    return (v) -> {
      try {
        selectedNavigation = v.getItemId();
        if (view.isSelected(selectedNavigation)) {
          return false;
        }
        view.setSelected(selectedNavigation);
        Fragment contentView = itemsCache.get(selectedNavigation);
        view.replaceAndCommit(contentView);
        return true;
      } finally {
        //this code already checks if the menu is open or not
        view.closeMenu();
      }
    };
  }

  @Override protected String getClassTag() {
    return MainActivityPresenter.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return GalleonApplication.isDebug();
  }

  private void createIfUsecaseNotExists(String query) {
    if (usecase == null) {
      usecase = new SearchCustomersUseCase.Builder()
          .query(query)
          .endpoint(endpoint)
          .build();
    } else {
      usecase = usecase.newBuilder()
          .query(query)
          .build();
    }
  }
}