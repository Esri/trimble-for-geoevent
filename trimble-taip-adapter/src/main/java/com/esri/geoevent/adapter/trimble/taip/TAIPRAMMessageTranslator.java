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

import com.esri.core.geometry.MapGeometry;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import com.esri.ges.core.geoevent.FieldException;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.messaging.MessagingException;
import com.esri.ges.util.GeometryUtil;

public class TAIPRAMMessageTranslator extends TAIPMessageTranslator
{
  @Override
  protected void translate(String trackId, ByteBuffer buf, GeoEvent geoEvent) throws MessagingException, FieldException
  {
    // AM Alarm
    // AAAAABBBCCCCCDDDDEEEEEFFFGGGHIJKK{L}
    // Total 33 + Length of Optional String
    
    int i = 0;
    geoEvent.setField(i++, trackId);

    // GPS Time of day 5 Sec AAAAA
    Integer time = convertToInteger(readString(buf, 5));
    geoEvent.setField(i++, (time != null) ? toTime(time, 0) : null);

    // Latitude 8 Deg BBB.CCCCC
    Long y = convertToLong(readString(buf, 8).replace('+', ' ').trim());

    // Longitude 9 Deg DDDD.EEEEE
    Long x = convertToLong(readString(buf, 9).replace('+', ' ').trim());

    if (x != null && y != null)
    {
      MapGeometry geometry = new MapGeometry(new Point(x * 0.00001, y * 0.00001), SpatialReference.create(4326));
      geoEvent.setField(i++, GeometryUtil.toJson(geometry));
    }

    // Speed 3 MPH FFF
    geoEvent.setField(i++, Short.parseShort(readString(buf, 3).trim()));

    // Heading 3 Deg GGG
    geoEvent.setField(i++, Short.parseShort(readString(buf, 3).trim()));

    // Alarm Code 1 n/a H
    geoEvent.setField(i++, readString(buf, 1).trim());

    // Source 1 n/a I
    // (0 = 2D GPS)
    // (1 = 3D GPS)
    // (2 = 2D DGPS)
    // (3 = 3D DGPS)
    // (6 = DR)
    // (8 = Degraded DR)
    // (9 = Unknown)
    // (4,5,7 = Not Defined)
    geoEvent.setField(i++, Short.parseShort(readString(buf, 1).trim()));

    // Age of Data Indicator 1 n/a J
    // (2 = Fresh, <10 sec)
    // (1 = Old, ≥10 sec)
    // (0 = Not available)
    geoEvent.setField(i++, Short.parseShort(readString(buf, 1)));

    // Length of Optional String (n) 2 n/a KK
    int optionalStringLength = Short.parseShort(readString(buf, 2));
    geoEvent.setField(i++, optionalStringLength);

    // Optional String n n/a {L}
    geoEvent.setField(i++, readString(buf, optionalStringLength));

    readIDField(buf, geoEvent, i);
  }
}
