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

public class TAIP0xF2MessageTranslator extends TAIPMessageTranslator
{
  @Override
  protected void translate(String trackId, ByteBuffer buf, GeoEvent geoEvent) throws MessagingException, FieldException
  {
    //LN Long Navigation Message
    //AAAAABBBCCCDDDDDDDEEEEFFFFFFFGGGGGGGHHIIIJKKKKLMMMNOO{PPQQ}RRRRRRRRRRST
    //Total 65 + (Number of SV's used times 4)
    
    int i = 0;
    geoEvent.setField(i++, trackId);
    Integer time = convertToInteger(readString(buf, 8)); //GPS Time of day 8 Sec AAAAA.BBB
    geoEvent.setField(i++, (time != null) ? toTime(time / 1000, time % 1000) : null);

    Long y = convertToLong(readString(buf, 10).replace('+', ' ').trim()); //Latitude 10 Deg CCC.DDDDDDD
    Long x = convertToLong(readString(buf, 11).replace('+', ' ').trim()); //Longitude 11 Deg EEEE.FFFFFFF
    if (x != null && y != null)
    {
    	MapGeometry geometry =  new MapGeometry(new Point(x * 0.0000001, y * 0.00000001), SpatialReference.create(4326));
      geoEvent.setField(i++, GeometryUtil.toJson(geometry));
    }
    geoEvent.setField(i++, convertToLong(readString(buf, 9).replace('+', ' ').trim()) * 0.01); //Altitude above MSL 9 Ft GGGGGGG.HH
    geoEvent.setField(i++, convertToLong(readString(buf, 4).replace('+', ' ').trim()) * 0.1);  //Horizontal Speed 4 MPH III.J
    geoEvent.setField(i++, convertToLong(readString(buf, 5).replace('+', ' ').trim()) * 0.1);  //Vertical Speed 5 MPH KKKK.L
    geoEvent.setField(i++, convertToLong(readString(buf, 4).replace('+', ' ').trim()) * 0.1);  //Heading 4 Deg MMM.N

    short num_svs = convertToShort(readString(buf, 2)); //Number of SVs used 2 n/a OO
    geoEvent.setField(i++, num_svs);
    String[] str_sv_id = new String[num_svs];
    String[] str_iode = new String[num_svs];
    for (int j = 0; j < num_svs; j++)
    {
      str_sv_id[j] = readString(buf, 2); //SV Id 2 n/a PP
      str_iode[j] = readString(buf, 2);  //IODE (2 digit hex) 2 n/a QQ
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
    geoEvent.setField(i++, readString(buf, 10)); //Reserved 10 n/a RRRRRRRRRR
    geoEvent.setField(i++, convertToShort(readString(buf, 1))); //Source 1 n/a S
    /*
    (0 = 2D GPS)
    (1 = 3D GPS)
    (2 = 2D DGPS)
    (3 = 3D DGPS)
    (6 = DR)
    (8 = Degraded DR)
    (9 = Unknown)
     */
    geoEvent.setField(i++, convertToShort(readString(buf, 1))); //Age of Data Indicator 1 n/a T
    /*
    (2 = Fresh, <10 sec)
    (1 = Old, ≥10 sec)
    (0 = Not available)
     */ 
    readIDField(buf, geoEvent, i);
  }
}