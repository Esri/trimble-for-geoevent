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
import java.util.TimeZone;

import com.esri.ges.core.geoevent.FieldException;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.messaging.GeoEventCreator;
import com.esri.ges.messaging.MessagingException;
import com.esri.ges.spatial.Spatial;

public abstract class TAIPMessageTranslator
{
  protected GeoEventCreator geoEventCreator;

  protected abstract void translate(String trackId, ByteBuffer buffer, GeoEvent geoEvent, Spatial spatial) throws MessagingException, FieldException;

  protected String readString(ByteBuffer from, int bytes) throws MessagingException
  {
    if (from.remaining() >= bytes)
    {
      byte[] buf = new byte[bytes];
      from.get(buf);
      return new String(buf);
    }
    throw new MessagingException("TAIP message field size is expected to be " + bytes + " bytes long but only " + from.remaining() + " bytes are actually available.");
  }

  protected Date toTime(Integer s, Integer ms)
  {
    if (s != null)
    {
      Calendar c = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
      int zh = s / 3600;
      int zm = s / 60 - zh * 60;
      int zs = s - (zh * 3600 + zm * 60);
      c.set(Calendar.HOUR_OF_DAY, zh);
      c.set(Calendar.MINUTE, zm);
      c.set(Calendar.SECOND, zs);
      c.set(Calendar.MILLISECOND, ms);
      return c.getTime();
    }
    return null;
  }

  public boolean isEmpty(String s)
  {
    return (s == null || s.length() == 0);
  }

  public Double convertToDouble(String s, Double defaultValue)
  {
    if (!isEmpty(s))
    {
      try
      {
        return Double.parseDouble(s.replaceAll(",", "."));
      }
      catch (Exception e)
      {
        ;
      }
    }
    return defaultValue;
  }

  public Double convertToDouble(Object value)
  {
    return (value != null) ? (value instanceof Double) ? (Double) value : convertToDouble(value.toString(), null) : null;
  }

  public Integer convertToInteger(Object value)
  {
    if (value != null)
    {
      if (value instanceof Integer)
        return (Integer) value;
      else
      {
        Double doubleValue = convertToDouble(value);
        if (doubleValue != null)
          return ((Long) Math.round(doubleValue)).intValue();
      }
    }
    return null;
  }

  public Short convertToShort(Object value)
  {
    if (value != null)
    {
      if (value instanceof Short)
        return (Short) value;
      else
      {
        Double doubleValue = convertToDouble(value);
        if (doubleValue != null)
          return ((Long) Math.round(doubleValue)).shortValue();
      }
    }
    return null;
  }

  public Long convertToLong(Object value)
  {
    if (value != null)
    {
      if (value instanceof Long)
        return (Long) value;
      else
      {
        Double doubleValue = convertToDouble(value);
        if (doubleValue != null)
          return Math.round(doubleValue);
      }
    }
    return null;
  }
}