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

import com.esri.ges.core.geoevent.FieldException;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.messaging.MessagingException;
import com.esri.ges.spatial.Spatial;

public class TAIP0xF2MessageTranslator extends TAIPMessageTranslator
{
  @Override
  protected void translate(String trackId, ByteBuffer buf, GeoEvent geoEvent, Spatial spatial) throws MessagingException, FieldException
  {
    int i = 0;
    geoEvent.setField(i++, trackId);
    geoEvent.setField(i++, secondsToTime(convertToInteger(readString(buf, 8))));

    Long y = convertToLong(readString(buf, 10).replace('+', ' ').trim());
    Long x = convertToLong(readString(buf, 11).replace('+', ' ').trim());
    if (x != null && y != null)
      geoEvent.setField(i++, spatial.createPoint(x * 0.0000001, y * 0.00000001, 4326).toJson());

    geoEvent.setField(i++, convertToLong(readString(buf, 9).replace('+', ' ').trim()) * 0.01);
    geoEvent.setField(i++, convertToLong(readString(buf, 4).replace('+', ' ').trim()) * 0.1);
    geoEvent.setField(i++, convertToLong(readString(buf, 5).replace('+', ' ').trim()) * 0.1);
    geoEvent.setField(i++, convertToLong(readString(buf, 4).replace('+', ' ').trim()) * 0.1);

    short num_svs = convertToShort(readString(buf, 2));
    geoEvent.setField(i++, num_svs);
    String[] str_sv_id = new String[num_svs];
    String[] str_iode = new String[num_svs];
    for (int j = 0; j < num_svs; j++)
    {
      str_sv_id[j] = readString(buf, 2);
      str_iode[j] = readString(buf, 2);
    }

    // How do we implement an unknown quantity of sv_id and iode values?
    String sv = "";
    for (int k = 0; k < str_sv_id.length; k++)
    {
      sv += str_sv_id[k] + ":" + str_iode[k];
      if (k < num_svs - 1)
      {
        sv += ",";
      }
    }
    geoEvent.setField(i++, sv);
    geoEvent.setField(i++, readString(buf, 10));
    geoEvent.setField(i++, convertToShort(readString(buf, 1)));
    geoEvent.setField(i++, convertToShort(readString(buf, 1)));
  }
}