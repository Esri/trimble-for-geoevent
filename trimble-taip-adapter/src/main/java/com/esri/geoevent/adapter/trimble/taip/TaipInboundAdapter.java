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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esri.ges.adapter.AdapterDefinition;
import com.esri.ges.adapter.InboundAdapterBase;
import com.esri.ges.core.component.ComponentException;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.framework.i18n.BundleLogger;
import com.esri.ges.framework.i18n.BundleLoggerFactory;

public class TaipInboundAdapter extends InboundAdapterBase
{
  private static final BundleLogger LOGGER = BundleLoggerFactory.getLogger(TaipInboundAdapter.class);

  private final Map<String, TAIPMessageTranslator> translators = new HashMap<String, TAIPMessageTranslator>();
  private final Map<String, String> lookup = new HashMap<String, String>();

  public TaipInboundAdapter(AdapterDefinition definition) throws ComponentException
  {
    super(definition);
    lookup.put("RPV", "TAIP0xF0");
    lookup.put("RCP", "TAIP0xF1");
    lookup.put("RLN", "TAIP0xF2");
    lookup.put("RAM", "TAIPRAM");
    translators.put("RPV", new TAIP0xF0MessageTranslator());
    translators.put("RCP", new TAIP0xF1MessageTranslator());
    translators.put("RLN", new TAIP0xF2MessageTranslator());
    translators.put("RAM", new TAIPRAMMessageTranslator());    
  }

  private class GeoEventProducer implements Runnable
  {
    private String channelId;
    private List<byte[]> messages;

    public GeoEventProducer(String channelId, List<byte[]> messages)
    {
      this.channelId = channelId;
      this.messages = messages;
    }

    @Override
    public void run()
    {
      while (!messages.isEmpty())
      {
        ByteBuffer message = ByteBuffer.wrap(messages.remove(0));
        if (message.remaining() >= 3)
        {
          byte[] chars = new byte[3];
          message.get(chars);
          String taipFormat = new String(chars);
          if (lookup.containsKey(taipFormat))
          {
            try
            {
              GeoEvent geoEvent = geoEventCreator.create(((AdapterDefinition) definition).getGeoEventDefinition(lookup.get(taipFormat)).getGuid());
              translators.get(taipFormat).translate(channelId, message, geoEvent);
              geoEventListener.receive(geoEvent);
            }
            catch (Throwable error)
            {
            	LOGGER.error("TRANSLATION_ERROR", error.getMessage());
            	LOGGER.info(error.getMessage(), error);
            }
          }
          else
          	LOGGER.error("TRANSLATION_ERROR_FORMAT_NOT_SUPPORTED", taipFormat);
        }
      }
    }
  }

  @Override
  public GeoEvent adapt(ByteBuffer buffer, String channelId)
  {
    // We don't need to implement anything in here because this method will
    // never get called. It would normally be called
    // by the base class's receive() method. However, we are overriding that
    // method, and our new implementation does not call
    // the adapter's adapt() method.
    return null;
  }
  
  @Override
  public void receive(ByteBuffer buffer, String channelId)
  {
    new Thread(new GeoEventProducer(channelId, index(buffer))).start();
  }

  @Override
  public void shutdown()
  {
    super.shutdown();
    translators.clear();
    lookup.clear();
  }

  private static List<byte[]> index(ByteBuffer in)
  {
    List<byte[]> messages = new ArrayList<byte[]>();
    for (int i = -1; in.hasRemaining();)
    {
      byte b = in.get();
      if (b == ((byte) '>')) // bom
      {
        i = in.position();
        in.mark();
      }
      else if (b == ((byte) '<')) // eom
      {
        if (i != -1)
        {
          byte[] message = new byte[in.position() - 1 - i];
          System.arraycopy(in.array(), i, message, 0, message.length);
          messages.add(message);
        }
        i = -1;
        in.mark();
      }
      else if (messages.isEmpty() && i == -1)
        in.mark();
    }
    return messages;
  }
}