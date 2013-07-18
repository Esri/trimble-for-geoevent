package com.esri.geoevent.adapter.trimble.taip;

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.esri.ges.adapter.AdapterDefinition;
import com.esri.ges.adapter.InboundAdapterBase;
import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.geoevent.FieldException;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.messaging.MessagingException;

public class TaipInboundAdapter extends InboundAdapterBase
{
  private static final Log LOG = LogFactory.getLog(TaipInboundAdapter.class);
  private final Map<String, TAIPMessageTranslator> translators = new HashMap<String, TAIPMessageTranslator>();

  public TaipInboundAdapter(AdapterDefinition definition) throws ComponentException
  {
    super(definition);
    translators.put("TAIP0xF0", new TAIP0xF0MessageTranslator());
    translators.put("TAIP0xF1", new TAIP0xF1MessageTranslator());
    translators.put("TAIP0xF2", new TAIP0xF2MessageTranslator());
  }

  @Override
  public GeoEvent adapt(ByteBuffer buffer, String channelID)
  {
    if (buffer == null)
      return null;

    // First we will scan through the data looking for a viable start-of-message indicator ">"
    buffer.mark();
    while (buffer.remaining() > 0 && buffer.get() != '>')
      buffer.mark();
    if (buffer.remaining() >= 3)
    {
      byte[] chars = new byte[3];
      buffer.get(chars);
      String edName = parseEventDefinitionName(new String(chars));
      if (translators.containsKey(edName))
        try
      {
          GeoEvent geoEvent = geoEventCreator.create(((AdapterDefinition)definition).getGeoEventDefinition(edName).getGuid());
          translators.get(edName).translate(channelID, buffer, geoEvent, spatial);
          buffer.mark();
          return geoEvent;
          // TODO Figure out what to do with Numberformat exceptions from bad input data.
      }
      catch (BufferUnderflowException ex)
      {
        buffer.reset();
      }
      catch (MessagingException e)
      {
        LOG.error("Exception while translating a TAIP message : " + e.getMessage());
      } catch (FieldException e)
      {
        LOG.error("Exception while translating a TAIP message : " + e.getMessage());
      }
    }
    else
      buffer.reset();
    return null;
  }

  private String parseEventDefinitionName(String msgFormat)
  {
    if ("RPV".equals(msgFormat))
      return "TAIP0xF0";
    else if ("RCP".equals(msgFormat))
      return "TAIP0xF1";
    else if ("RLN".equals(msgFormat))
      return "TAIP0xF2";
    return null;
  }
}