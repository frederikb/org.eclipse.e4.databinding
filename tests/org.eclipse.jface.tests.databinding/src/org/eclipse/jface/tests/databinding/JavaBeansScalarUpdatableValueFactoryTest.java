package org.eclipse.jface.tests.databinding;

import junit.framework.TestCase;

import org.eclipse.jface.internal.provisional.databinding.DataBinding;
import org.eclipse.jface.internal.provisional.databinding.IDataBindingContext;
import org.eclipse.jface.internal.provisional.databinding.IUpdatableFactory;
import org.eclipse.jface.internal.provisional.databinding.IUpdatableValue;
import org.eclipse.jface.internal.provisional.databinding.Property;
import org.eclipse.jface.internal.provisional.databinding.beans.JavaBeansScalarUpdatableValueFactory;

public class JavaBeansScalarUpdatableValueFactoryTest extends TestCase {
   
   public static class TestBean {
      private String field = "Hello, world";

      public String getField() {
         return field;
      }

      public void setField(String field) {
         this.field = field;
      }
   }
   
   public void test_getUpdatableValue() throws Exception {
      TestBean test = new TestBean();
      
      IDataBindingContext dbc = DataBinding.createContext(new IUpdatableFactory[] {
              new JavaBeansScalarUpdatableValueFactory()
        });
      IUpdatableValue updatable = (IUpdatableValue) dbc.createUpdatable(new Property(test, "field"));
      assertEquals("Hello, world", updatable.getValue());
   }
   
}
