package org.emmef.servicemanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import org.emmef.servicemanager.ProviderVisitor.VisitState;

public abstract class AbstractServiceManager<T> {
	private final Object lock = new Object[0];
	private final boolean rememberServicesLoaded;
	private final Class<T> classToProvide;
	
	private final AtomicReference<List<T>> providers = new AtomicReference<>();
	private final AtomicBoolean servicesLoaded = new AtomicBoolean();
	private final Comparator<T> comparator;
	
	protected AbstractServiceManager(Class<T> classToProvide, boolean rememberServicesLoaded, Comparator<T> comparator) {
		if (classToProvide == null) {
			throw new NullPointerException("Need a class to provide");
		}
		this.classToProvide = classToProvide;
		this.rememberServicesLoaded = rememberServicesLoaded;
		this.comparator = comparator;
	}
	
	protected final VisitState visit(ProviderVisitor<T> visitor) {
		List<T> list = providers.get();
		if (list == null || list.isEmpty()) {
			return VisitState.NONE;
		}
		return visitor.visit(new ArrayList<T>(list));
	}

	public final void loadFromServiceLoader(ClassLoader classLoader) {
		if (rememberServicesLoaded && !servicesLoaded.compareAndSet(false, true)) {
			return;
		}
		ServiceLoader<T> serviceLoader = ServiceLoader.load(classToProvide, classLoader);
		addProviders(serviceLoader.iterator());
	}
	
	public final void loadFromServiceLoader() {
		loadFromServiceLoader(Thread.currentThread().getContextClassLoader());
	}

	public final void addProvider(final T provider) {
		addProviders(new SingleElementIterator(provider));
	}
	
	public Class<T> getClassToProvide() {
		return classToProvide;
	}
	
	private void addProviders(Iterator<T> providers) {
		if (!providers.hasNext()) {
			return;
		}
		
		synchronized (lock) {
			List<T> currentProviderList = this.providers.get();
			/*
			 * We always create a new array instance, so that the lock-free
			 * getters always see a consistent value.
			 */
			List<T> providerList = new ArrayList<>();
			// Add current and new items
			if (currentProviderList != null) {
				providerList.addAll(currentProviderList);
			}
			while (providers.hasNext()) {
				T provider = providers.next();
				if (!providerList.contains(provider)) {
					providerList.add(provider);
				}
			}
			if (comparator != null) {
				Collections.sort(providerList, comparator);
			}
			this.providers.set(providerList);
		}
	}

	private final class SingleElementIterator implements Iterator<T> {
		private T provider;

		private SingleElementIterator(T provider) {
			this.provider = provider;
		}

		@Override
		public boolean hasNext() {
			return provider != null;
		}

		@Override
		public T next() {
			if (!hasNext()) {
				T result = provider;
				provider = null;
				return result;
			}
			throw new NoSuchElementException();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
