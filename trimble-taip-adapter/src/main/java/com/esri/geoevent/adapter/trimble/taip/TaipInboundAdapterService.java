package com.esri.geoevent.adapter.trimble.taip;

import com.esri.ges.adapter.Adapter;
import com.esri.ges.adapter.AdapterServiceBase;
import com.esri.ges.adapter.util.XmlAdapterDefinition;
import com.esri.ges.core.component.ComponentException;

public class TaipInboundAdapterService extends AdapterServiceBase
{
  public TaipInboundAdapterService() throws ComponentException
  {
    definition = new XmlAdapterDefinition(getResourceAsStream("adapter-definition.xml"));
  }

  @Override
  public Adapter createAdapter() throws ComponentException
  {
    return new TaipInboundAdapter(definition);
  }
}