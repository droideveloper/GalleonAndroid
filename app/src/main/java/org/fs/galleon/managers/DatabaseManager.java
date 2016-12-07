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
package org.fs.galleon.managers;

import android.content.Context;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import java.util.List;
import org.fs.core.AbstractOrmliteHelper;
import org.fs.galleon.GalleonApplication;
import org.fs.galleon.R;
import org.fs.galleon.entities.Syncable;
import rx.Observable;

public final class DatabaseManager extends AbstractOrmliteHelper implements IDatabaseManager {

  private final static String DB_NAME = "sync.db";
  private final static int DB_VERSION = 1;

  private RuntimeExceptionDao<Syncable, Long> syncables;

  public DatabaseManager(Context context) {
    super(context, DB_NAME, DB_VERSION, R.raw.ormlite_config);
  }

  @Override protected void createTables(ConnectionSource conn) throws SQLException {
    TableUtils.createTable(conn, Syncable.class);
  }

  @Override protected void dropTables(ConnectionSource conn) throws SQLException {
    TableUtils.dropTable(conn, Syncable.class, false);
  }

  @Override protected boolean isLogEnabled() {
    return GalleonApplication.isDebug();
  }

  @Override protected String getClassTag() {
    return DatabaseManager.class.getSimpleName();
  }

  @Override public Observable<Syncable> firstOrDefault(int remoteId) {
    checkIfSyncableDaoCreated();
    return Observable.just(syncables.queryForAll())
                     .flatMap(Observable::from)
                     .filter(entity -> entity.getRemoteId() == remoteId)
                     .firstOrDefault(null);
  }

  @Override public Observable<List<Syncable>> everything() {
    checkIfSyncableDaoCreated();
    return Observable.just(syncables.queryForAll());
  }

  @Override public Observable<Syncable> save(Syncable sync) {
    checkIfSyncableDaoCreated();
    return Observable.just(sync)
                     .flatMap(x -> {
                       boolean created = syncables.create(x) == 1;
                       return created ? Observable.just(x) : Observable.empty();
                     });
  }

  @Override public Observable<Syncable> update(Syncable sync) {
    checkIfSyncableDaoCreated();
    return Observable.just(sync)
                     .flatMap(x -> {
                        if(x.getSyncableId() != null) {
                          boolean updated = syncables.update(x) == 1;//does the update
                          return updated ? Observable.just(x) : Observable.empty();
                        }  else {
                          return save(sync);//returns create if id is null
                        }
                     });
  }

  @Override public Observable<Boolean> remove(long id) {
    checkIfSyncableDaoCreated();
    return Observable.just(id)
                     .flatMap(x -> Observable.just(syncables.deleteById(x) == 1));
  }

  private void checkIfSyncableDaoCreated() {
    if(syncables == null) {
      syncables = getRuntimeExceptionDao(Syncable.class);
    }
  }
}
