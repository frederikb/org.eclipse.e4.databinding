package org.eclipse.core.internal.databinding.provisional.bind;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.databinding.property.value.IValueProperty;
import org.eclipse.core.runtime.CoreException;

/**
 * @since 1.5
 * @provisional This class has been added as part of a work in progress. It is
 *              not guaranteed to work or remain the same in future releases.
 *              For more information contact e4-dev@eclipse.org.
 */
public class Bind {

	/**
	 * This is the ITwoWayBinding that sits immediately on top on the model
	 * observable.
	 * 
	 * @param <V>
	 */
	static class TwoWayModelBinding<V> extends TwoWayBinding<V> {
		private final IObservableValue<V> modelObservable;

		private final boolean takeOwnership;

		private boolean isModelChanging = false;

		IValueChangeListener<V> modelListener = new IValueChangeListener<V>() {
			public void handleValueChange(ValueChangeEvent<V> event) {
				if (!isModelChanging) {
					targetBinding.setTargetValue(event.diff.getNewValue());
				}
			}
		};

		/**
		 * 
		 * @param modelObservable
		 * @param takeOwnership
		 *            <code>true</code> if this class is to take ownership of
		 *            the given model observable, which means it has the
		 *            responsibility of disposing of it when this binding is
		 *            disposed, <code>false</code> if the given model observable
		 *            must not be disposed of by this class
		 */
		public TwoWayModelBinding(IObservableValue<V> modelObservable,
				boolean takeOwnership) {
			super(true);
			this.modelObservable = modelObservable;
			this.takeOwnership = takeOwnership;

			modelObservable.addValueChangeListener(modelListener);
		}

		public V getModelValue() {
			return modelObservable.getValue();
		}

		public void setModelValue(V newValue) {
			isModelChanging = true;
			try {
				modelObservable.setValue(newValue);
			} finally {
				isModelChanging = false;
			}
		}

		public void removeModelListener() {
			modelObservable.removeValueChangeListener(modelListener);

			/*
			 * If we 'own' the observable then we must dispose of it, if we
			 * don't 'own' the observable then we must not dispose of it.
			 */
			if (takeOwnership) {
				modelObservable.dispose();
			}
		}
	}

	/**
	 * This is the IOneWayBinding that sits immediately on top on the model
	 * observable.
	 * 
	 * @param <V>
	 */
	static class OneWayModelBinding<V> extends OneWayBinding<V> {
		private final IObservableValue<V> modelObservable;

		private final boolean takeOwnership;

		IValueChangeListener<V> modelListener = new IValueChangeListener<V>() {
			public void handleValueChange(ValueChangeEvent<V> event) {
				targetBinding.setTargetValue(event.diff.getNewValue());
			}
		};

		/**
		 * 
		 * @param modelObservable
		 * @param takeOwnership
		 *            <code>true</code> if this class is to take ownership of
		 *            the given model observable, which means it has the
		 *            responsibility of disposing of it when this binding is
		 *            disposed, <code>false</code> if the given model observable
		 *            must not be disposed of by this class
		 */
		public OneWayModelBinding(IObservableValue<V> modelObservable,
				boolean takeOwnership) {
			this.modelObservable = modelObservable;
			this.takeOwnership = takeOwnership;

			modelObservable.addValueChangeListener(modelListener);
		}

		public V getModelValue() {
			return modelObservable.getValue();
		}

		public void removeModelListener() {
			modelObservable.removeValueChangeListener(modelListener);

			/*
			 * If we 'own' the observable then we must dispose of it, if we
			 * don't 'own' the observable then we must not dispose of it.
			 */
			if (takeOwnership) {
				modelObservable.dispose();
			}
		}
	}

	/**
	 * Initiates two-way binding.
	 * <P>
	 * The model observable is not disposed when this binding is disposed.
	 * 
	 * @param modelObservable
	 * @return an object that can chain one-way bindings
	 */
	public static <V> IOneWayBinding<V> oneWay(
			IObservableValue<V> modelObservable) {
		return new OneWayModelBinding<V>(modelObservable, false);
	}

	/**
	 * This is a convenience method that creates an observable for the model
	 * from the given property and source. The observable will be disposed when
	 * the binding is disposed.
	 * 
	 * @param modelProperty
	 * @param source
	 * @return an object that can chain one-way bindings
	 */
	public static <S, V> IOneWayBinding<V> oneWay(
			IValueProperty<S, V> modelProperty, S source) {
		IObservableValue<V> modelObservable = modelProperty.observe(source);
		return new OneWayModelBinding<V>(modelObservable, true);
	}

	/**
	 * Initiates two-way binding.
	 * <P>
	 * The model observable is not disposed when this binding is disposed.
	 * 
	 * @param modelObservable
	 * @return an object that can chain two-way bindings
	 */
	public static <V> ITwoWayBinding<V> twoWay(
			IObservableValue<V> modelObservable) {
		return new TwoWayModelBinding<V>(modelObservable, false);
	}

	/**
	 * This is a convenience method that creates an observable for the model
	 * from the given property and source. The observable will be disposed when
	 * the binding is disposed.
	 * 
	 * @param modelProperty
	 * @param source
	 * @return an object that can chain two-way bindings
	 */
	public static <S, V> ITwoWayBinding<V> twoWay(
			IValueProperty<S, V> modelProperty, S source) {
		IObservableValue<V> modelObservable = modelProperty.observe(source);
		return new TwoWayModelBinding<V>(modelObservable, true);
	}

	/**
	 * This method is used to 'bounce back' a value from the target.
	 * Specifically this means whenever the target value changes, the value is
	 * converted using the given converter (targetToModel). The resulting value
	 * is the converted back using the same converter (modelToTarget).
	 * <P>
	 * A use case for this method is as follows. You have a number that is
	 * stored in the model as an Integer. You want to display the number in a
	 * text box with separators, so 1234567 would be displayed as 1,234,567 or
	 * 1.234.567 depending on your regional settings. You want to allow more
	 * flexibility on what the user can enter. For example you may want to allow
	 * the user to miss out the separators. As the user types, the value is
	 * updated in the model. When the control loses focus you want the
	 * separators to be inserted in the Text control in the proper positions.
	 * <P>
	 * To do this you create two bindings. One is a two-way binding that
	 * observes the Text control with SWT.Modify. The other is a 'bounce back'
	 * that observes the control with SWT.FocusOut. It might be coded as
	 * follows:
	 * <P>
	 * <code>
	 * 		Bind.bounceBack(myIntegerToTextConverter)
		.to(SWTObservables.observeText(textControl, SWT.FocusOut));
	 * </code>
	 * 
	 * @param converter
	 * @return an object that can chain two-way bindings
	 */
	public static <T1, T2> ITwoWayBinding<T2> bounceBack(
			final IBidiConverter<T1, T2> converter) {
		return new TwoWayBinding<T2>(false) {

			public T2 getModelValue() {
				/*
				 * This method should never be called because pullInitialValue
				 * is set to false.
				 */
				throw new UnsupportedOperationException();
			}

			public void setModelValue(T2 valueFromTarget) {
				try {
					T1 modelSideValue = converter
							.targetToModel(valueFromTarget);
					T2 valueBackToTarget = converter
							.modelToTarget(modelSideValue);
					this.targetBinding.setTargetValue(valueBackToTarget);
				} catch (CoreException e) {
					/*
					 * No bounce-back occurs if the value from the target side
					 * cannot be converted. We do nothing because the user will
					 * typically have an error indicator anyway.
					 */
				}
			}

			public void removeModelListener() {
				/*
				 * Nothing to do here because nothing originates from the model
				 * side.
				 */
			}
		};
	}

}
