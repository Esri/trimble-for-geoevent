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
    int i = 0;
    geoEvent.setField(i++, trackId);
    geoEvent.setField(i++, toTime(convertToInteger(readString(buf, 5)), 0));
    Integer y = convertToInteger(readString(buf, 8).replace('+', ' ').trim());
    Integer x = convertToInteger(readString(buf, 9).replace('+', ' ').trim());
    if (x != null && y != null)
    {
    	MapGeometry geometry =  new MapGeometry(new Point(x * 0.00001, y * 0.00001), SpatialReference.create(4326));
      geoEvent.setField(i++, GeometryUtil.toJson(geometry));
    }
    geoEvent.setField(i++, convertToShort(readString(buf, 3)));
    geoEvent.setField(i++, convertToShort(readString(buf, 3)));
    geoEvent.setField(i++, convertToShort(readString(buf, 1)));
    geoEvent.setField(i++, convertToShort(readString(buf, 1)));
  }
}