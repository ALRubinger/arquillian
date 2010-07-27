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
package org.jboss.arquillian.performance.event;

import org.jboss.arquillian.spi.Context;
import org.jboss.arquillian.spi.TestContextAppender;
import org.jboss.arquillian.spi.event.suite.Test;

/**
 * 
 * A PerformanceTestContextAppender.
 * 
 * @author <a href="mailto:stale.pedersen@jboss.org">Stale W. Pedersen</a>
 * @version $Revision: 1.1 $
 */
public class PerformanceTestContextAppender implements TestContextAppender
{

   public void append(Context context)
   {
      context.register(Test.class, new TestPerformanceVerifier());
      context.register(Test.class, new PerformanceResultStore());
      

      
      
   }

}