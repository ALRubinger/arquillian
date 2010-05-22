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
package org.jboss.arquillian.glassfish.embedded30;

import java.io.File;
import java.net.URL;

import org.glassfish.api.deployment.DeployCommandParameters;
import org.glassfish.api.deployment.UndeployCommandParameters;
import org.glassfish.api.embedded.ContainerBuilder;
import org.glassfish.api.embedded.EmbeddedContainer;
import org.glassfish.api.embedded.EmbeddedFileSystem;
import org.glassfish.api.embedded.Server;
import org.jboss.arquillian.protocol.servlet.ServletMethodExecutor;
import org.jboss.arquillian.spi.Configuration;
import org.jboss.arquillian.spi.ContainerMethodExecutor;
import org.jboss.arquillian.spi.Context;
import org.jboss.arquillian.spi.DeployableContainer;
import org.jboss.arquillian.spi.DeploymentException;
import org.jboss.arquillian.spi.LifecycleException;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.glassfish.api.ShrinkwrapReadableArchive;

/**
 * GlassFishEmbeddedContainer
 *
 * @author <a href="mailto:aslak@conduct.no">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class GlassFishEmbeddedContainer implements DeployableContainer
{
   private String target = "server";
   private Server server;

   private GlassFishConfiguration configuration;
   
   public GlassFishEmbeddedContainer()
   {
   }
   
   public void setup(Context context, Configuration configuration)
   {
      this.configuration = configuration.getContainerConfig(GlassFishConfiguration.class);
      
      final Server.Builder builder = new Server.Builder(GlassFishEmbeddedContainer.class.getName());

      final EmbeddedFileSystem.Builder embeddedFsBuilder = new EmbeddedFileSystem.Builder();
      final EmbeddedFileSystem embeddedFs = embeddedFsBuilder
               .instanceRoot(
                     new File(
                           this.configuration.getInstanceRoot()))
               .autoDelete(this.configuration.getAutoDelete())
               .build();
      builder.embeddedFileSystem(embeddedFs);
      
      server = builder.build();

      server.addContainer(ContainerBuilder.Type.all);
   }
   
   public void start(Context context) throws LifecycleException
   {
      try 
      {
         for(EmbeddedContainer contianer : server.getContainers()) {
            contianer.bind(server.createPort(configuration.getBindPort()), "http");
            contianer.start();
         }
      } 
      catch (Exception e) 
      {
         throw new LifecycleException("Could not start container", e);
      }
   }

   public void stop(Context context) throws LifecycleException
   {
      try 
      {
         server.stop();
      } 
      catch (Exception e) 
      {
         throw new LifecycleException("Could not stop container", e);
      }
   }

   public ContainerMethodExecutor deploy(Context context, Archive<?> archive) throws DeploymentException
   {
      try 
      {
         DeployCommandParameters params = new DeployCommandParameters();
         params.enabled = true;
         params.target = target;
         params.name = createDeploymentName(archive.getName());
         
         server.getDeployer().deploy(
               archive.as(ShrinkwrapReadableArchive.class),
               params);
         
      } 
      catch (Exception e) 
      {
         throw new DeploymentException("Could not deploy " + archive.getName(), e);
      }

      try 
      {
         return new ServletMethodExecutor(
               new URL(
                     "http",
                     "localhost",
                     configuration.getBindPort(), 
                     "/")
               );
      } 
      catch (Exception e) 
      {
         throw new RuntimeException("Could not create ContianerMethodExecutor", e);
      }
   }

   public void undeploy(Context context, Archive<?> archive) throws DeploymentException
   {
      UndeployCommandParameters params = new UndeployCommandParameters();
      params.target = target;
      params.name = createDeploymentName(archive.getName());
      try 
      {
         server.getDeployer().undeploy(params.name, params);
      }
      catch (Exception e) 
      {
         throw new DeploymentException("Could not undeploy " + archive.getName(), e);
      }
   }
   
   private String createDeploymentName(String archiveName) 
   {
      return archiveName.substring(0, archiveName.lastIndexOf("."));
   }
}