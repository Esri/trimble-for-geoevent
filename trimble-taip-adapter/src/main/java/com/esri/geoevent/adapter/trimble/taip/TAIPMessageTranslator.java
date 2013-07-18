package com.esri.geoevent.adapter.trimble.taip;

import java.nio.ByteBuffer;
import java.util.Calendar;
import java.util.Date;

import com.esri.ges.core.geoevent.FieldException;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.messaging.GeoEventCreator;
import com.esri.ges.messaging.MessagingException;
import com.esri.ges.spatial.Spatial;

public abstract class TAIPMessageTranslator
{
  protected GeoEventCreator geoEventCreator;

  protected abstract void translate(String trackId, ByteBuffer buffer, GeoEvent geoEvent, Spatial spatial) throws MessagingException, FieldException;

  protected String readString(ByteBuffer from, int bytes)
  {
    byte[] buf = new byte[bytes];
    from.get(buf);
    return new String(buf);
  }

  protected Date secondsToTime(int seconds)
  {
    int zh;
    int zm;
    int zs;

    Calendar c = Calendar.getInstance();

    zh = seconds / 3600;
    zm = seconds / 60 - zh * 60;
    zs = seconds - (zh * 3600 + zm * 60);

    c.set(Calendar.HOUR, zh);
    c.set(Calendar.MINUTE, zm);
    c.set(Calendar.SECOND, zs);

    return c.getTime();
  }
}