package javafx.beans.property;

import com.sun.javafx.binding.ExpressionHelper;
import java.lang.ref.WeakReference;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.binding.LongBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableLongValue;
import javafx.beans.value.ObservableNumberValue;
import javafx.beans.value.ObservableValue;

public abstract class LongPropertyBase extends LongProperty {
   private long value;
   private ObservableLongValue observable = null;
   private InvalidationListener listener = null;
   private boolean valid = true;
   private ExpressionHelper helper = null;

   public LongPropertyBase() {
   }

   public LongPropertyBase(long var1) {
      this.value = var1;
   }

   public void addListener(InvalidationListener var1) {
      this.helper = ExpressionHelper.addListener(this.helper, this, (InvalidationListener)var1);
   }

   public void removeListener(InvalidationListener var1) {
      this.helper = ExpressionHelper.removeListener(this.helper, var1);
   }

   public void addListener(ChangeListener var1) {
      this.helper = ExpressionHelper.addListener(this.helper, this, (ChangeListener)var1);
   }

   public void removeListener(ChangeListener var1) {
      this.helper = ExpressionHelper.removeListener(this.helper, var1);
   }

   protected void fireValueChangedEvent() {
      ExpressionHelper.fireValueChangedEvent(this.helper);
   }

   private void markInvalid() {
      if (this.valid) {
         this.valid = false;
         this.invalidated();
         this.fireValueChangedEvent();
      }

   }

   protected void invalidated() {
   }

   public long get() {
      this.valid = true;
      return this.observable == null ? this.value : this.observable.get();
   }

   public void set(long var1) {
      if (!this.isBound()) {
         if (this.value != var1) {
            this.value = var1;
            this.markInvalid();
         }

      } else {
         throw new RuntimeException((this.getBean() != null && this.getName() != null ? this.getBean().getClass().getSimpleName() + "." + this.getName() + " : " : "") + "A bound value cannot be set.");
      }
   }

   public boolean isBound() {
      return this.observable != null;
   }

   public void bind(final ObservableValue var1) {
      if (var1 == null) {
         throw new NullPointerException("Cannot bind to null");
      } else {
         Object var2;
         if (var1 instanceof ObservableLongValue) {
            var2 = (ObservableLongValue)var1;
         } else if (var1 instanceof ObservableNumberValue) {
            final ObservableNumberValue var3 = (ObservableNumberValue)var1;
            var2 = new LongBinding() {
               {
                  super.bind(var1);
               }

               protected long computeValue() {
                  return var3.longValue();
               }
            };
         } else {
            var2 = new LongBinding() {
               {
                  super.bind(var1);
               }

               protected long computeValue() {
                  Number var1x = (Number)var1.getValue();
                  return var1x == null ? 0L : var1x.longValue();
               }
            };
         }

         if (!var2.equals(this.observable)) {
            this.unbind();
            this.observable = (ObservableLongValue)var2;
            if (this.listener == null) {
               this.listener = new Listener(this);
            }

            this.observable.addListener(this.listener);
            this.markInvalid();
         }

      }
   }

   public void unbind() {
      if (this.observable != null) {
         this.value = this.observable.get();
         this.observable.removeListener(this.listener);
         this.observable = null;
      }

   }

   public String toString() {
      Object var1 = this.getBean();
      String var2 = this.getName();
      StringBuilder var3 = new StringBuilder("LongProperty [");
      if (var1 != null) {
         var3.append("bean: ").append(var1).append(", ");
      }

      if (var2 != null && !var2.equals("")) {
         var3.append("name: ").append(var2).append(", ");
      }

      if (this.isBound()) {
         var3.append("bound, ");
         if (this.valid) {
            var3.append("value: ").append(this.get());
         } else {
            var3.append("invalid");
         }
      } else {
         var3.append("value: ").append(this.get());
      }

      var3.append("]");
      return var3.toString();
   }

   private static class Listener implements InvalidationListener {
      private final WeakReference wref;

      public Listener(LongPropertyBase var1) {
         this.wref = new WeakReference(var1);
      }

      public void invalidated(Observable var1) {
         LongPropertyBase var2 = (LongPropertyBase)this.wref.get();
         if (var2 == null) {
            var1.removeListener(this);
         } else {
            var2.markInvalid();
         }

      }
   }
}
