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

import java.util.List;

import com.esri.ges.spatial.Envelope;
import com.esri.ges.spatial.Geometry;
import com.esri.ges.spatial.GeometryException;
import com.esri.ges.spatial.GeometryType;
import com.esri.ges.spatial.MultiPoint;
import com.esri.ges.spatial.Point;
import com.esri.ges.spatial.Polygon;
import com.esri.ges.spatial.Polyline;
import com.esri.ges.spatial.Spatial;
import com.esri.ges.spatial.SpatialReference;

public class TestSpatial implements Spatial
{

  private static final long serialVersionUID = 1L;

  @Override
  public int getWkid()
  {
    return 0;
  }

  @Override
  public void setWkid(int wkid)
  {
    ;
  }

  @Override
  public SpatialReference getSpatialReference()
  {
    return null;
  }

  @Override
  public Geometry aggregate(List<Geometry> geometries, GeometryType type)
  {
    return null;
  }

  @Override
  public Point createPoint(double x, double y, int wkid)
  {
    return new Point()
    {
      private static final long serialVersionUID = 1L;
      
      @Override
      public void setSpatialReference(SpatialReference arg0)
      {
        ;
      }
      
      @Override
      public GeometryType getType()
      {
        return GeometryType.Point;
      }
      
      @Override
      public SpatialReference getSpatialReference()
      {
        return new SpatialReference()
        {
          private static final long serialVersionUID = 1L;

          @Override
          public void setWkid(int wkid)
          {
            ;
          }
          
          @Override
          public int getWkid()
          {
            return 4326;
          }
          
          @Override
          public boolean equals(SpatialReference sr)
          {
            return true;
          }
        };
      }
      
      @Override
      public void setZ(double z)
      {
        ;
      }
      
      @Override
      public void setY(double y)
      {
        ;
      }
      
      @Override
      public void setX(double x)
      {
        ;
      }
      
      @Override
      public double getZ()
      {
        return 0;
      }
      
      @Override
      public double getY()
      {
        return 0;
      }
      
      @Override
      public double getX()
      {
        return 0;
      }
      
      @Override
      public boolean equals(Point point)
      {
        return false;
      }
      
      @Override
      public String toJson()
      {
        StringBuffer sb = new StringBuffer();
        sb.append("{\"x\":").append(0);
        sb.append(", \"y\":").append(0);
        sb.append(", \"z\":").append(0);
        sb.append(", \"spatialReference\":{\"wkid\":").append(getSpatialReference().getWkid()).append("}}");
        return sb.toString();
      }
    };
  }

  @Override
  public Point createPoint(double x, double y, double z, int wkid)
  {
    return null;
  }

  @Override
  public Geometry fromJson(String json) throws GeometryException
  {
    return null;
  }

  @Override
  public Envelope queryEnvelope(Geometry g)
  {
    return null;
  }

  @Override
  public MultiPoint createMultiPoint()
  {
    return null;
  }

  @Override
  public Polyline createPolyline()
  {
    return null;
  }

  @Override
  public Polygon createPolygon()
  {
    return null;
  }

  @Override
  public Envelope createEnvelope(double xmin, double ymin, double xmax, double ymax, int wkid)
  {
    return null;
  }

  @Override
  public boolean inside(Geometry g1, Geometry g2)
  {
    return false;
  }

  @Override
  public boolean outside(Geometry g1, Geometry g2)
  {
    return false;
  }

  @Override
  public boolean enter(Geometry g1, Geometry previous, Geometry last)
  {
    return false;
  }

  @Override
  public boolean exit(Geometry g1, Geometry previous, Geometry last)
  {
    return false;
  }

  @Override
  public boolean equals(Geometry g1, Geometry g2)
  {
    return false;
  }

  @Override
  public int getWkidFromText(String wkText)
  {
    return 0;
  }

}
