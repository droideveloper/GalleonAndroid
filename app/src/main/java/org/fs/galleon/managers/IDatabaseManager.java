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

import java.util.List;
import org.fs.galleon.entities.Syncable;
import rx.Observable;

public interface IDatabaseManager {

  /**
   *
   * @param remoteId
   * @return
   */
  Observable<Syncable>        firstOrDefault(int remoteId);

  /**
   *
   * @return
   */
  Observable<List<Syncable>>  everything();

  /**
   *
   * @param sync
   * @return
   */
  Observable<Syncable>        save(Syncable sync);

  /**
   *
   * @param sync
   * @return
   */
  Observable<Syncable>        update(Syncable sync);

  /**
   *
   * @param id
   * @return
   */
  Observable<Boolean>         remove(long id);
}
