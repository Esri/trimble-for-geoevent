/*
  Copyright 1995-2013 Esri

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

  For additional information, contact:
  Environmental Systems Research Institute, Inc.
  Attn: Contracts Dept
  380 New York Street
  Redlands, California, USA 92373

  email: contracts@esri.com
*/

package com.esri.geoevent.adapter.trimble.taip;

import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;

import com.esri.ges.core.geoevent.FieldException;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.messaging.GeoEventCreator;
import com.esri.ges.messaging.MessagingException;
import com.esri.ges.spatial.Spatial;

public abstract class TAIPMessageTranslator
{
  protected GeoEventCreator geoEventCreator;

  protected abstract void translate(String trackId, ByteBuffer buffer, GeoEvent geoEvent, Spatial spatial) throws MessagingException, FieldException;

  protected String readString(ByteBuffer from, int bytes)
  {
    byte[] buf = new byte[bytes];
    from.get(buf);
    return new String(buf);
  }

  protected Date secondsToTime(int seconds)
  {
    int zh;
    int zm;
    int zs;

    Calendar c = Calendar.getInstance();

    zh = seconds / 3600;
    zm = seconds / 60 - zh * 60;
    zs = seconds - (zh * 3600 + zm * 60);

    c.set(Calendar.HOUR, zh);
    c.set(Calendar.MINUTE, zm);
    c.set(Calendar.SECOND, zs);

    return c.getTime();
  }
}