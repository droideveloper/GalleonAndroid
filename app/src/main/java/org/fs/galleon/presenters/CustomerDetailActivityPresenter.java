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

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import com.jakewharton.rxbinding.support.v7.widget.RxToolbar;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import org.fs.common.AbstractPresenter;
import org.fs.common.BusManager;
import org.fs.common.ThreadManager;
import org.fs.exception.AndroidException;
import org.fs.galleon.GalleonApplication;
import org.fs.galleon.R;
import org.fs.galleon.commons.SimpleCallbackImp;
import org.fs.galleon.entities.Customer;
import org.fs.galleon.entities.Document;
import org.fs.galleon.entities.Syncable;
import org.fs.galleon.events.ApproveEvent;
import org.fs.galleon.events.DocumentEvent;
import org.fs.galleon.events.PositionParcelable;
import org.fs.galleon.managers.IDatabaseManager;
import org.fs.galleon.managers.IFileManager;
import org.fs.galleon.nets.ICloud;
import org.fs.galleon.nets.IEndpoint;
import org.fs.galleon.nets.Response;
import org.fs.galleon.usecases.CustomerDocumentsUseCase;
import org.fs.galleon.usecases.ICustomerDocumentsUseCase;
import org.fs.galleon.views.ApproveDialogFragmentView;
import org.fs.galleon.views.ICustomerDetailActivityView;
import org.fs.galleon.views.viewholders.DocumentRecyclerViewHolder;
import org.fs.util.Collections;
import org.fs.util.InvokeUtility;
import org.fs.util.StringUtility;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class CustomerDetailActivityPresenter extends AbstractPresenter<ICustomerDetailActivityView>
    implements ICustomerDetailActivityPresenter, SimpleCallbackImp.SwipeCallback {

  public final static String KEY_CUSTOMER_ENTITY = "customer.entity";
  private final static String KEY_CUSTOMER_DOCUMENTS = "customer.documents";

  private final static String TAG_DELETE = "approve.delete";

  private final IEndpoint endpoint;
  private final IDatabaseManager dbManager;
  private final ICloud cloud;
  private final IFileManager fileManager;

  private Customer customer;
  private List<Document> documents;

  private Subscription viewToolbarSub;
  private Subscription eventSub;
  private CustomerDocumentsUseCase usecase;

  public CustomerDetailActivityPresenter(ICustomerDetailActivityView view, IEndpoint endpoint, IDatabaseManager dbManager, ICloud cloud, IFileManager fileManager) {
    super(view);
    this.endpoint = endpoint;
    this.dbManager = dbManager;
    this.cloud = cloud;
    this.fileManager = fileManager;
  }

  @Override public void onCreate() {
    view.setUpViews();
  }

  @Override public void onStart() {
    view.showProgress();
    if (customer != null) {
      String titleStr = String.format(Locale.getDefault(), "%s %s %s",
          customer.getFirstName(),
          StringUtility.isNullOrEmpty(customer.getMiddleName()) ? StringUtility.EMPTY : customer.getMiddleName(),
          customer.getLastName());
      view.setTitleText(titleStr);
      view.setSubTitleText(customer.getIdentityNo());
    }
    if (!Collections.isNullOrEmpty(documents)) {
      view.bindDocumentAdapter(documents);
      view.hideProgress();
    } else {
      if (!Collections.isNullOrEmpty(customer.getContacts())) {
        view.bindContactAdapter(customer.getContacts());
      }
      //build usecase
      usecase = new CustomerDocumentsUseCase.Builder()
          .customerId(customer.getCustomerId())
          .endpoint(endpoint)
          .build();
      //execute usecase
      usecase.executeAsync(new ICustomerDocumentsUseCase.Callback() {

        @Override public void onSuccess(Response<List<Document>> response) {
          if (response.isSuccess()) {
            documents = response.data();
            view.bindDocumentAdapter(documents);
          } else {
            view.showError(
                String.format(Locale.ENGLISH, "ErrorCode :%d\nErrorMessage : %s\n", response.code(), response.message())
            );
          }
        }

        @Override public void onError(Throwable thr) {
          log(thr);
        }

        @Override public void onCompleted() {
          view.hideProgress();
        }
      });
    }
    eventSub = BusManager.add((evt) -> {
      if (evt instanceof ApproveEvent) {
        ApproveEvent event = (ApproveEvent) evt;
        if (event.isApproved()) {
          if (event.result() != null) {
            PositionParcelable position = (PositionParcelable) event.result();
            view.removeDocumentAt(position.getAdapterPosition());
          }
        }
      } else if (evt instanceof DocumentEvent) {
        DocumentEvent event = (DocumentEvent) evt;
        if (event.isDelete()) {
          view.showProgressDelete();
          deleteIfLocalExists(event.entity())
              .flatMap(this::deleteIfRemoteExists)
              .subscribeOn(Schedulers.io())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(response -> {
                if (response.isSuccess()) {
                  view.showError(
                      String.format(Locale.getDefault(), view.getContext()
                          .getString(R.string.messageDocumentDeletedSuccessfully),
                          event.entity().getDocumentName())
                  );
                }
              }, this::log, view::hideProgressDelete);
        } else if(event.isDownload()) {
          BusManager.send(new DocumentEvent(DocumentEvent.DocumentAction.DOWNLOAD_START, event.entity()));
          downloadIfRemoteExists(event.entity());
        }
      }
    });
  }

  @Override public void onStop() {
    if (viewToolbarSub != null) {
      viewToolbarSub.unsubscribe();
      viewToolbarSub = null;
    }
    if (eventSub != null) {
      BusManager.remove(eventSub);
      eventSub = null;
    }
    if (usecase != null) {
      usecase.cancelIfActive();
      usecase = null;
    }
  }

  @Override public void restoreState(Bundle restoreState) {
    if (restoreState != null) {
      if (restoreState.containsKey(KEY_CUSTOMER_ENTITY)) {
        customer = restoreState.getParcelable(KEY_CUSTOMER_ENTITY);
      }
      if (restoreState.containsKey(KEY_CUSTOMER_DOCUMENTS)) {
        documents = restoreState.getParcelableArrayList(KEY_CUSTOMER_DOCUMENTS);
      }
    }
  }

  @Override public void storeState(Bundle storeState) {
    if (customer != null) {
      storeState.putParcelable(KEY_CUSTOMER_ENTITY, customer);
    }
    if (!Collections.isNullOrEmpty(documents)) {
      storeState.putParcelableArrayList(KEY_CUSTOMER_DOCUMENTS, new ArrayList<>(documents));
    }
  }

  @Override public void observeToolbar(Toolbar viewToolbar) {
    viewToolbarSub = RxToolbar.navigationClicks(viewToolbar)
        .subscribe(this::closeView);
  }

  @Override public void observeRecycler(RecyclerView viewRecycler) {
    ItemTouchHelper touchHelper = new ItemTouchHelper(SimpleCallbackImp.swipeEndHelper(this));
    touchHelper.attachToRecyclerView(viewRecycler);
  }

  @Override public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
    String messageStr = view.getContext().getString(R.string.messageApproveDeleteDocumentStr);
    //we retrieve true type of viewHolder then bind it's data to our dialogView
    DocumentRecyclerViewHolder docxViewHolder = (DocumentRecyclerViewHolder) viewHolder;
    final Document docx = docxViewHolder.getDocument();
    //format message
    messageStr = String.format(Locale.getDefault(), messageStr, docx.getDocumentName());
    ApproveDialogFragmentView.newInstance(messageStr, new PositionParcelable(viewHolder.getAdapterPosition()))
        .show(view.getSupportFragmentManager(), TAG_DELETE);
  }

  @Override public void onBackPressed() {
    InvokeUtility.invoke(() -> closeView(null));
  }

  @Override protected void log(Throwable error) {
    super.log(error);
    view.hideProgress();
  }

  @Override protected String getClassTag() {
    return CustomerDetailActivityPresenter.class.getSimpleName();
  }

  @Override protected boolean isLogEnabled() {
    return GalleonApplication.isDebug();
  }

  private void closeView(Void ignored) {
    view.finish();
  }

  private Observable<Document> deleteIfLocalExists(Document docx) {
    return dbManager.firstOrDefault(docx.getDocumentId())
        .flatMap(syncable -> {
          if (!StringUtility.isNullOrEmpty(syncable)) {
            //it can be only available in remote path
            File file = new File(syncable.getLocalPath());
            if (file.exists()) {
              boolean deleted = file.delete();
              if (deleted) {
                log(Log.INFO,
                    String.format(Locale.ENGLISH, "file deleted successfully, '%s'", syncable.getLocalPath())
                );
              }
            }
          }
          return Observable.just(docx);
        });
  }

  private Observable<Response<Boolean>> deleteIfRemoteExists(Document docx) {
    return cloud.deleteContent(docx.getDocumentId());
  }

  private File createIfLocalNotExists(Document docx) {
    File syncDirectory = fileManager.syncDirectory();
    if (syncDirectory == null) {
      ThreadManager.runOnUiThread(() -> view.showError("You should select a sync directory"));
      return null;
    }
    File customerDirectory = new File(syncDirectory, String.valueOf(docx.getDirectoryId()));
    if (!customerDirectory.exists()) {
      boolean success = customerDirectory.mkdirs();
      if (success) {
        log(Log.INFO,
            String.format(Locale.getDefault(), "'%s' is created.", customerDirectory.getAbsolutePath())
        );
      }
    }
    return customerDirectory;
  }

  private File createIfLocalTempNotExists(File root) {
    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.US);
    File dir = new File(root, "/dcache");
    if (!dir.exists()) {
      boolean success = dir.mkdirs();
      if (success) {
        log(Log.INFO,
            String.format(Locale.ENGLISH, "%s is created, as temp dir.", dir.getAbsolutePath())
        );
      }
    }
   return new File(dir, "temp_" + format.format(new Date()) + ".gz");
  }

  private void downloadIfRemoteExists(Document docx) {
     cloud.downloadContent(docx.getDocumentId())
        .flatMap(body -> {
          try {
            File file = createIfLocalTempNotExists(fileManager.defaultDirectory());
            byte[] buffer = new byte[8192];
            OutputStream out = new FileOutputStream(file);
            InputStream input = body.byteStream();
            int read;
            while((read = input.read(buffer)) != -1) {
              out.write(buffer, 0, read);
            }
            input.close();
            out.flush();
            out.close();
            return Observable.just(file);
          } catch (Exception error) {
            throw new AndroidException(error);
          }
        })
        .flatMap(f -> {
          try {
            File file = new File(createIfLocalNotExists(docx),  docx.getDocumentName());
           return fileManager.toDecompressedFile(file, new GZIPInputStream(new FileInputStream(f)));
          } catch (IOException ioError) {
            throw new AndroidException(ioError);
          }
        })
        .flatMap(f -> {
          if (f.exists()) {
            boolean success = f.setLastModified(docx.getUpdateDate()
                .getTime());
            if(success) {
              log(Log.INFO,
                  String.format(Locale.getDefault(), "%s date is set.", f.getAbsolutePath())
              );
            }
            Syncable syncable = new Syncable.Builder()
                .fileName(f.getName())
                .localPath(f.getAbsolutePath())
                .lastModifiedTime(docx.getUpdateDate())
                .remoteId(docx.getDocumentId())
                .build();
            return dbManager.save(syncable);
          }
          return Observable.empty();
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(s -> {
          File file = new File(s.getLocalPath());
          if (file.exists()) {
            log(Log.INFO,
                String.format(Locale.getDefault(), "%s created.", file.getAbsolutePath())
            );
            view.showError(
                String.format(Locale.getDefault(), "%s is downloaded successfully, tap on for viewing it.",
                    file.getName())
            );
          }
        }, this::log,
        () -> BusManager.send(new DocumentEvent(DocumentEvent.DocumentAction.DOWNLOAD_FINISH, docx)));
  }
}