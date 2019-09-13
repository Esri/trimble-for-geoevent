/*
  Copyright 1995-2019 Esri

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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import com.esri.ges.core.geoevent.FieldException;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.framework.i18n.BundleLogger;
import com.esri.ges.framework.i18n.BundleLoggerFactory;
import com.esri.ges.messaging.GeoEventCreator;
import com.esri.ges.messaging.MessagingException;

public abstract class TAIPMessageTranslator
{
  private static final BundleLogger LOGGER = BundleLoggerFactory.getLogger(TaipInboundAdapter.class);

  protected GeoEventCreator         geoEventCreator;

  protected abstract void translate(String trackId, ByteBuffer buffer, GeoEvent geoEvent) throws MessagingException, FieldException;

  protected String readString(ByteBuffer from, int bytes) throws MessagingException
  {
    if (from.remaining() >= bytes)
    {
      byte[] buf = new byte[bytes];
      from.get(buf);
      return new String(buf);
    }
    throw new MessagingException(LOGGER.translate("MESSAGE_SIZE_VALIDATION", bytes, from.remaining()));
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

  public void readIDField(ByteBuffer buf, GeoEvent geoEvent, int i) throws MessagingException, FieldException
  {

    String remainderString = "";
    try
    {
      StringBuilder remainder = new StringBuilder();
      String next = "";
      while (!"<".equals(next) && !">".equals(next) && buf.hasRemaining())
      {
        buf.mark();
        next = readString(buf, 1);
        if (">".equals(next))
        {
          // Start of next message, reset and break while loop
          buf.reset();
          break;
        }
        else
        {
          remainder.append(next);
        }
      }

      final String[] remValueArray = { null, null };
      remainderString = remainder.toString();
      if (remainderString != null && !remainderString.isEmpty())
      {
        remainderString = remainderString.replace("<", "").replace(">", "");
        LOGGER.trace("Parsing remaining message string {0}", remainderString);

        String[] finalFields = remainderString.split(";");
        Arrays.stream(finalFields).forEach(remString ->
          {
            if (remString != null && !remString.trim().isEmpty())
            {
              LOGGER.trace("Parsing remaining message string fragment {0}", remString);
              if (remString.toUpperCase().startsWith("ID="))
              {
                remValueArray[0] = remString.substring(3);
              }
              else if (remString.startsWith("*"))
              {
                remValueArray[1] = remString.substring(1);
              }
            }
          });
      }

      if (remValueArray[0] != null)
      {
        String id = remValueArray[0].trim();
        if (!id.isEmpty())
        {
          LOGGER.trace("Setting event ID: {0}", id);
          geoEvent.setField(i++, id);
        }
      }
    }
    catch (Exception e)
    {
      LOGGER.debug("Failed to parse ID field (remainder: {0}), moving on...", e, remainderString);
    }
  }

}
