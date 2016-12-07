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
package org.fs.galleon.utils;

public final class GsonEntity {

  public final class Response {
    public final static String KEY_CODE     = "Code";
    public final static String KEY_MESSAGE  = "Message";
    public final static String KEY_DATA     = "Data";
  }

  public final class Category {
    public final static String KEY_CATEGORY_ID    = "CategoryID";
    public final static String KEY_CATEGORY_NAME  = "CategoryName";
  }

  public final class City {
    public final static String KEY_CITY_ID    = "CityID";
    public final static String KEY_COUNTRY_ID = "CountryID";
    public final static String KEY_CITY_NAME  = "CityName";
  }

  public final class Credential {
    public final static String KEY_USER_NAME  = "UserName";
    public final static String KEY_PASSWORD   = "Password";
  }

  public final class Contact {
    public final static String KEY_CITY_ID      = "CityID";
    public final static String KEY_COUNTRY_ID   = "CountryID";
    public final static String KEY_CONTACT_NAME = "ContactName";
    public final static String KEY_ADDRESS      = "Address";
    public final static String KEY_CUSTOMER_ID  = "CustomerID";
    public final static String KEY_PHONE        = "Phone";
    public final static String KEY_COUNTRY      = "Country";
    public final static String KEY_CITY         = "City";
  }

  public final class Country {
    public final static String KEY_COUNTRY_ID   = "CountryID";
    public final static String KEY_COUNTRY_NAME = "CountryName";
  }

  public final class Customer {
    public final static String KEY_CUSTOMER_ID  = "CustomerID";
    public final static String KEY_FIRST_NAME   = "FirstName";
    public final static String KEY_MIDDLE_NAME  = "MiddleName";
    public final static String KEY_LAST_NAME    = "LastName";
    public final static String KEY_IDENTITY_NO  = "Identity";
    public final static String KEY_CATEGORY_ID  = "CategoryID";
    public final static String KEY_CATEGORY     = "Category";
    public final static String KEY_CONTACTS     = "Contacts";
  }

  public final class Directory {
    public final static String KEY_DIRECTORY_ID         = "DirectoryID";
    public final static String KEY_PARENT_DIRECTORY_ID  = "ParentDirectoryID";
    public final static String KEY_CUSTOMER_ID          = "CustomerID";
    public final static String KEY_DIRECTORY_NAME       = "DirectoryName";
    public final static String KEY_PARENT               = "Parent";
    public final static String KEY_DOCUMENTS            = "Documents";
  }

  public final class Document {
    public final static String KEY_DOCUMENT_ID    = "DocumentID";
    public final static String KEY_DIRECTORY_ID   = "DirectoryID";
    public final static String KEY_CUSTOMER_ID    = "CustomerID";
    public final static String KEY_DOCUMENT_NAME  = "DocumentName";
    public final static String KEY_CONTENT_TYPE   = "ContentType";
    public final static String KEY_CONTENT_LENGTH = "ContentLength";
    public final static String KEY_CREATE_DATE    = "CreateDate";
    public final static String KEY_UPDATE_DATE    = "UpdateDate";
  }

  public final class Session {
    public final static String KEY_TOKEN = "Token";
  }
}
