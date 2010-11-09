/*
 * JBoss, Home of Professional Open Source
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.impl.configuration;

import org.jboss.arquillian.impl.configuration.api.ContainerDef;
import org.jboss.arquillian.impl.configuration.api.ProtocolDef;
import org.jboss.arquillian.impl.configuration.model.ArquillianModel;
import org.jboss.arquillian.impl.configuration.model.ContainerImpl;
import org.jboss.arquillian.impl.configuration.model.PropertyImpl;
import org.jboss.arquillian.impl.configuration.model.ProtocolImpl;

/**
 * ContainerDefImpl
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ContainerDefImpl extends ArquillianDescriptorImpl implements ContainerDef
{
   private ContainerImpl container;
   
   public ContainerDefImpl(ArquillianModel model, ContainerImpl container)
   {
      super(model);
      this.container = container;
   }
   
   //-------------------------------------------------------------------------------------||
   // Required Implementations - ContainerDescriptor -------------------------------------||
   //-------------------------------------------------------------------------------------||

   /* (non-Javadoc)
    * @see org.jboss.arquillian.impl.configuration.api.ContainerDef#setDefault()
    */
   @Override
   public ContainerDef setDefault()
   {
      container.setDefault(true);
      return this;
   }
   
   /* (non-Javadoc)
    * @see org.jboss.arquillian.impl.configuration.api.ContainerDescription#dependency(java.lang.String)
    */
   @Override
   public ContainerDef dependency(String artifactId)
   {
      container.getDependencies().add(artifactId);
      return this;
   }
   
   /* (non-Javadoc)
    * @see org.jboss.arquillian.impl.configuration.api.ContainerDescription#protocol()
    */
   @Override
   public ProtocolDef protocol(String type)
   {
      ProtocolImpl protocol = new ProtocolImpl(type);
      container.getProtocols().add(protocol);
      return new ProtocolDefImpl(getSchemaModel(), container, protocol);
   }
   
   /* (non-Javadoc)
    * @see org.jboss.arquillian.impl.configuration.api.ContainerDescription#property(java.lang.String, java.lang.String)
    */
   @Override
   public ContainerDef property(String name, String value)
   {
      container.getProperties().add(new PropertyImpl(name, value));
      return this;
   }
}