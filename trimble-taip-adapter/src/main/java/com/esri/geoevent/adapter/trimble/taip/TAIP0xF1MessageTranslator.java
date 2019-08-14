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

public class TAIP0xF1MessageTranslator extends TAIPMessageTranslator
{
  @Override
  protected void translate(String trackId, ByteBuffer buf, GeoEvent geoEvent) throws MessagingException, FieldException
  {
    // CP Compact Position Solution
    // AAAAABBBCCCCDDDDEEEEFG
    // Total number of characters is 22
    int i = 0;
    geoEvent.setField(i++, trackId);

    // GPS Time of day 5 Sec AAAAA
    geoEvent.setField(i++, toTime(convertToInteger(readString(buf, 5)), 0));

    // Latitude 7 Deg BBBCCCC
    Integer y = convertToInteger(readString(buf, 7).replace('+', ' ').trim());

    // Longitude 8 Deg DDDDEEEE
    Integer x = convertToInteger(readString(buf, 8).replace('+', ' ').trim());
    if (x != null && y != null)
    {
      MapGeometry geometry = new MapGeometry(new Point(x * 0.0001, y * 0.0001), SpatialReference.create(4326));
      geoEvent.setField(i++, GeometryUtil.toJson(geometry));
    }

    // Source 1 n/a F
    // 0 = 2D GPS
    // 1 = 3D GPS
    // 2 = 2D DGPS
    // 3 = 3D DGPS
    // 6 = DR
    // 8 = Degraded DR
    // 9 = Unknown
    // 4,5,7 = Not Defined
    geoEvent.setField(i++, convertToShort(readString(buf, 1)));

    // Age of Data Indicator 1 n/a G
    // 2 = Fresh, <10 sec
    // 1 = Old, >10 sec
    // 0 = Not available
    geoEvent.setField(i++, convertToShort(readString(buf, 1)));

    readIDField(buf, geoEvent, i);
  }
}
