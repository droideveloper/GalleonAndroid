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
import org.fs.galleon.entities.Customer;

public final class CustomerSelectedEvent implements IEvent {

  private final Customer selectedCustomer;

  public CustomerSelectedEvent(final Customer selectedCustomer) {
    this.selectedCustomer = selectedCustomer;
  }

  public Customer selectedCustomer() {
    return this.selectedCustomer;
  }
}
