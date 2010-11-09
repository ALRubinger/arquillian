/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.arquillian.impl.client.container;

import java.util.Arrays;

import org.jboss.arquillian.impl.AbstractManagerTestBase;
import org.jboss.arquillian.impl.core.ManagerBuilder;
import org.jboss.arquillian.impl.core.spi.context.ContainerContext;
import org.jboss.arquillian.impl.domain.Container;
import org.jboss.arquillian.impl.domain.ContainerRegistry;
import org.jboss.arquillian.spi.client.container.ContainerConfiguration;
import org.jboss.arquillian.spi.client.container.DeployableContainer;
import org.jboss.arquillian.spi.core.InstanceProducer;
import org.jboss.arquillian.spi.core.annotation.ApplicationScoped;
import org.jboss.arquillian.spi.core.annotation.Inject;
import org.jboss.arquillian.spi.event.container.AfterStart;
import org.jboss.arquillian.spi.event.container.BeforeStart;
import org.jboss.arquillian.spi.event.suite.BeforeSuite;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

/**
 * StartContainerTestCase
 *
 * @author <a href="mailto:aknutsen@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
@RunWith(MockitoJUnitRunner.class)
public class ContainerStarterTestCase extends AbstractManagerTestBase
{
   @Inject @ApplicationScoped
   private InstanceProducer<ContainerRegistry> containerRegistry;

   @Override
   protected void addExtensions(ManagerBuilder builder)
   {
      builder.extension(ContainerStarter.class);
   }
   
   @Test(expected = IllegalStateException.class)
   @Ignore // currently no exception handling
   public void shouldThrowIllegalStateOnMissingDeployableContainer() throws Exception
   {
//      ContainerStarter handler = new ContainerStarter();
//      handler.callback(context, new SuiteEvent());
   }
   
   @Test
   public void shouldStartTheDeployableContainer() throws Exception
   {
      Container container = Mockito.mock(Container.class);
      final DeployableContainer<ContainerConfiguration> deployableContainer = Mockito.mock(DeployableContainer.class);
      ContainerRegistry registry = Mockito.mock(ContainerRegistry.class);
      containerRegistry.set(registry);

      Mockito.when(container.getName()).thenReturn("_TEST_");
      Mockito.when(container.getDeployableContainer()).thenAnswer(new Answer<DeployableContainer<?>>()
      {
         @Override
         public DeployableContainer<?> answer(InvocationOnMock invocation) throws Throwable
         {
            return deployableContainer;
         }
      });
      Mockito.when(registry.getContainers()).thenReturn(Arrays.asList(container));

      fire(new BeforeSuite());
      
      // verify that the contianer was started
      Mockito.verify(deployableContainer).start();
   
      // verify that all the events where fired
      assertEventFired(BeforeStart.class, 1);
      assertEventFiredInContext(BeforeStart.class, ContainerContext.class);
      assertEventFired(AfterStart.class, 1);
      assertEventFiredInContext(AfterStart.class, ContainerContext.class);
   }
}
