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
package org.fs.galleon.events;

import org.fs.common.IEvent;
import org.fs.galleon.entities.Document;

public final class DocumentEvent implements IEvent {

  private final DocumentAction action;
  private final Document document;

  public DocumentEvent(DocumentAction action, Document document) {
    this.action = action;
    this.document = document;
  }

  public boolean isDownload() {
    return action.equals(DocumentAction.DOWNLOAD);
  }

  public boolean isDelete() {
    return action.equals(DocumentAction.DELETE);
  }

  public boolean isView() {
    return action.equals(DocumentAction.VIEW);
  }

  public boolean isDownloadStart() {
    return action.equals(DocumentAction.DOWNLOAD_START);
  }

  public boolean isDownloadFinish() {
    return action.equals(DocumentAction.DOWNLOAD_FINISH);
  }

  public Document entity() {
    return this.document;
  }

  public enum DocumentAction {
    DOWNLOAD,
    DOWNLOAD_START,
    DOWNLOAD_FINISH,
    VIEW,
    DELETE
  }
}