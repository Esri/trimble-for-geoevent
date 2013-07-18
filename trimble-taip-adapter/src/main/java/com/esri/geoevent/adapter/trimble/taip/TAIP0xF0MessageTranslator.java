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