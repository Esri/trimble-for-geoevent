package com.esri.geoevent.adapter.trimble.taip;

import java.nio.ByteBuffer;

import com.esri.ges.core.geoevent.FieldException;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.spatial.Point;
import com.esri.ges.spatial.Spatial;

public class TAIP0xF2MessageTranslator extends TAIPMessageTranslator
{
  @Override
  protected void translate(String trackId, ByteBuffer buf, GeoEvent geoEvent, Spatial spatial) throws FieldException
  {
    int i = 0;
    geoEvent.setField(i++, trackId);
    geoEvent.setField(i++, secondsToTime(Integer.parseInt(readString(buf, 8))));

    double y = Long.parseLong(readString(buf, 10).replace('+', ' ').trim()) * 0.00000001;
    double x = Long.parseLong(readString(buf, 11).replace('+', ' ').trim()) * 0.0000001;
    int wkid = 4326;
    Point point = spatial.createPoint(x, y, wkid);
    geoEvent.setField(i++, point.toJson());

    geoEvent.setField(i++, Long.parseLong(readString(buf, 9).replace('+', ' ').trim()) * 0.01);
    geoEvent.setField(i++, Long.parseLong(readString(buf, 4).replace('+', ' ').trim()) * 0.1);
    geoEvent.setField(i++, Long.parseLong(readString(buf, 5).replace('+', ' ').trim()) * 0.1);
    geoEvent.setField(i++, Long.parseLong(readString(buf, 4).replace('+', ' ').trim()) * 0.1);

    short num_svs = Short.parseShort(readString(buf, 2));
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
    geoEvent.setField(i++, Short.parseShort(readString(buf, 1)));
    geoEvent.setField(i++, Short.parseShort(readString(buf, 1)));
  }
}