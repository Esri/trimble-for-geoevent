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
import com.esri.ges.spatial.Point;
import com.esri.ges.spatial.Spatial;

public class TAIP0xF0MessageTranslator extends TAIPMessageTranslator
{
  @Override
  protected void translate(String trackId, ByteBuffer buf, GeoEvent geoEvent, Spatial spatial) throws FieldException
  {
    int i = 0;
    geoEvent.setField(i++, trackId);
    geoEvent.setField(i++, secondsToTime(Integer.parseInt(readString(buf, 5))));

    double y = Integer.parseInt(readString(buf, 8).replace('+', ' ').trim()) * 0.00001;
    double x = Integer.parseInt(readString(buf, 9).replace('+', ' ').trim()) * 0.00001;
    int wkid = 4326;
    Point point = spatial.createPoint(x, y, wkid);
    geoEvent.setField(i++, point.toJson() );

    geoEvent.setField(i++, Short.parseShort(readString(buf, 3)));
    geoEvent.setField(i++, Short.parseShort(readString(buf, 3)));
    geoEvent.setField(i++, Short.parseShort(readString(buf, 1)));
    geoEvent.setField(i++, Short.parseShort(readString(buf, 1)));
  }
}