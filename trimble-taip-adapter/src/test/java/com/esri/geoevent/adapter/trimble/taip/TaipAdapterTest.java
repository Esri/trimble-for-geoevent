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

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.esri.ges.adapter.util.XmlAdapterDefinition;
import com.esri.ges.core.geoevent.FieldException;
import com.esri.ges.core.geoevent.FieldGroup;
import com.esri.ges.core.geoevent.GeoEvent;
import com.esri.ges.core.geoevent.GeoEventDefinition;
import com.esri.ges.core.geoevent.GeoEventPropertyName;
import com.esri.ges.manager.geoeventdefinition.GeoEventDefinitionManager;
import com.esri.ges.messaging.GeoEventCreator;
import com.esri.ges.messaging.MessagingException;
import com.esri.ges.spatial.Geometry;
import com.esri.ges.spatial.Spatial;

public class TaipAdapterTest
{
  TaipInboundAdapter adapter;

  @Before
  public void setUp() throws Exception
  {
    GeoEventCreator factory = new GeoEventCreator()
    {

      @Override
      public GeoEvent create(String guid)
      {
        return new GeoEvent()
        {
          private static final long serialVersionUID = -7302846106394204530L;

          @Override
          public boolean isMutable()
          {
            return true;
          }

          @Override
          public void setAsImmutable()
          {
            ;
          }

          @Override
          public GeoEvent clone(GeoEvent parent)
          {
            return this;
          }

          @Override
          public GeoEventDefinition getGeoEventDefinition()
          {
            return null;
          }

          @Override
          public Object[] getAllFields()
          {
            return null;
          }

          @Override
          public void setAllFields(Object[] atts)
          {
            ;
          }

          @Override
          public Set<Entry<GeoEventPropertyName, Object>> getProperties()
          {
            return null;
          }

          @Override
          public boolean hasProperty(GeoEventPropertyName name)
          {
            return false;
          }

          @Override
          public Object getProperty(GeoEventPropertyName name)
          {
            return null;
          }

          @Override
          public void setProperty(String name, Object value)
          {

          }

          @Override
          public void setProperty(GeoEventPropertyName name, Object value)
          {

          }

          @Override
          public String getTrackId()
          {
            return null;
          }

          @Override
          public Geometry getGeometry()
          {
            return null;
          }

          @Override
          public void setGeometry(Geometry geometry)
          {
            ;
          }

          @Override
          public Geometry getGeometry(String name)
          {
            return null;
          }

          @Override
          public void setGeometry(String name, Geometry geometry)
          {
            ;
          }

          @Override
          public byte[] toByteArray()
          {
            return null;
          }

          @Override
          public String getGuid()
          {
            return null;
          }

          @Override
          public Date getStartTime()
          {
            return null;
          }

          @Override
          public void setField(int i, Object value)
          {
            ;
          }

          @Override
          public void setField(String attName, Object value)
          {
            ;
          }

          @Override
          public FieldGroup createFieldGroup(String groupName) throws FieldException
          {
            return null;
          }

          @Override
          public Object getField(int i)
          {
            return null;
          }

          @Override
          public Object getField(String attName)
          {
            return null;
          }

          @Override
          public List<?> getFields(int i) throws FieldException
          {
            return null;
          }

          @Override
          public List<?> getFields(String attName) throws FieldException
          {
            return null;
          }

          @Override
          public FieldGroup getFieldGroup(int i) throws FieldException
          {
            return null;
          }

          @Override
          public FieldGroup getFieldGroup(String attName) throws FieldException
          {
            return null;
          }

          @Override
          public List<FieldGroup> getFieldGroups(int i) throws FieldException
          {
            return null;
          }

          @Override
          public List<FieldGroup> getFieldGroups(String attName) throws FieldException
          {
            return null;
          }
        };
      }

      @Override
      public GeoEvent create(String name, String owner) throws MessagingException
      {
        return null;
      }

      @Override
      public GeoEventDefinitionManager getGeoEventDefinitionManager()
      {
        return null;
      }

      @Override
      public GeoEvent create(String guid, Object[] values) throws MessagingException
      {
        return null;
      }
    };
    // TODO - Spatial spatial = new TestSpatial();
    adapter = new TaipInboundAdapter(new XmlAdapterDefinition(this.getClass().getClassLoader().getResourceAsStream("adapter-definition.xml")));
    adapter.setGeoEventCreator(factory);
    // TODO - adapter.setSpatial(spatial);
  }

  @After
  public void tearDown() throws Exception
  {
  }

  @Test
  public void testProcessBuffer() throws IOException
  {
    testBinaryFile("/TAIP0xF0.dat", 5);
    testBinaryFile("/TAIP0xF1.dat", 5);
    testBinaryFile("/TAIP0xF2.dat", 5);
  }

  private void testBinaryFile(String fileName, int messages) throws IOException
  {
    String channel = "";
    int currentMsg = 0;
    InputStream in = getClass().getResourceAsStream(fileName);
    ByteBuffer buf = ByteBuffer.allocate(1024);
    buf.clear();
    int batchSize = 5;

    while (currentMsg < messages)
    {
      byte[] batch = new byte[batchSize];
      int bytesRead = in.read(batch);
      if (bytesRead < 0)
        Assert.fail("Ran out of data in the input file " + fileName);
      buf.put(batch, 0, bytesRead);
      buf.flip();
      
      GeoEvent message = adapter.adapt(buf, channel);
      buf.compact();
      if (message != null)
      {
        currentMsg++;
      }
    }
  }

}
