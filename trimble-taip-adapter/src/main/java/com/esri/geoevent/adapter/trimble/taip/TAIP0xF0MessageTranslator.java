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

public class TAIP0xF0MessageTranslator extends TAIPMessageTranslator
{
  @Override
  protected void translate(String trackId, ByteBuffer buf, GeoEvent geoEvent) throws MessagingException, FieldException
  {
    // PV Position/Velocity Solution
    // AAAAABBBCCCCCDDDDEEEEEFFFGGGHI
    // Total 30
    // >RPV15714+3739438-1220384601512612;ID=1234;*7F<
    // >RPV06624-3189567+1160078800027302;ID=AAFKF;*39<

    int i = 0;
    geoEvent.setField(i++, trackId);

    // AAAAA GPS Time of day 5 Sec AAAAA
    geoEvent.setField(i++, toTime(convertToInteger(readString(buf, 5)), 0));

    // Latitude 8 Deg BBB.CCCCC
    Integer y = convertToInteger(readString(buf, 8).replace('+', ' ').trim());

    // Longitude 9 Deg DDDD.EEEEE
    Integer x = convertToInteger(readString(buf, 9).replace('+', ' ').trim());
    if (x != null && y != null)
    {
      MapGeometry geometry = new MapGeometry(new Point(x * 0.00001, y * 0.00001), SpatialReference.create(4326));
      geoEvent.setField(i++, GeometryUtil.toJson(geometry));
    }

    // Speed 3 MPH FFF
    geoEvent.setField(i++, convertToShort(readString(buf, 3)));

    // Heading 3 Deg GGG
    geoEvent.setField(i++, convertToShort(readString(buf, 3)));

    // Source 1 n/a I
    // (0 = 2D GPS)
    // (1 = 3D GPS)
    // (2 = 2D DGPS)
    // (3 = 3D DGPS)
    // (6 = DR)
    // (8 = Degraded DR)
    // (9 = Unknown)
    geoEvent.setField(i++, convertToShort(readString(buf, 1)));

    // Age of Data Indicator 1 n/a J
    // (2 = Fresh, <10 sec)
    // (1 = Old, ≥10 sec)
    // (0 = Not available)
    geoEvent.setField(i++, convertToShort(readString(buf, 1)));

    readIDField(buf, geoEvent, i);
  }
}
