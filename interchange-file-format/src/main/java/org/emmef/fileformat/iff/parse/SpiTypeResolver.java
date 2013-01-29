package org.emmef.fileformat.iff.parse;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.emmef.servicemanager.AbstractServiceManager;
import org.emmef.servicemanager.ProviderVisitor;

public enum SpiTypeResolver implements TypeResolver {
	INSTANCE;

	private static final ServiceManager SERVICEMANAGER = new ServiceManager();
	private static final Object lock = new Object[0];
	private static Map<String, TypeBuilderFactory> map;

	@Override
	public TypeBuilderFactory get(String identifier) {
		return ensureMapping().get(identifier);
	}

	private static Map<String, TypeBuilderFactory> ensureMapping() {
		synchronized (lock) {
			if (map != null) {
				return map;
			}
			map = discoverFactories();
			return map;
		}
	}

	private static Map<String, TypeBuilderFactory> discoverFactories() {
		SERVICEMANAGER.loadFromServiceLoader();
		return SERVICEMANAGER.createFactories();
	}
	
	private static class ServiceManager extends AbstractServiceManager<InterchangeFormatProvider> {
		ServiceManager() {
			super(InterchangeFormatProvider.class, true, new Comparator<InterchangeFormatProvider>() {

				@Override
				public int compare(InterchangeFormatProvider o1, InterchangeFormatProvider o2) {
					return Long.compare(o2.priority(), o1.priority());
				}
			});
		}
		
		Map<String, TypeBuilderFactory> createFactories() {
			final Map<String, TypeBuilderFactory> result = new HashMap<>();
			
			visit(new ProviderVisitor<InterchangeFormatProvider>() {
				@Override
				public org.emmef.servicemanager.ProviderVisitor.VisitState visit(List<InterchangeFormatProvider> list) {
					for (InterchangeFormatProvider provider : list) {
						if (!result.containsKey(provider.getTypeIdentifier())) {
							result.put(provider.getTypeIdentifier(), provider.get());
							// TODO: print an error message as logging is fixed
						}
					}
					
					return result.isEmpty() ? VisitState.NONE : VisitState.SUCCESS;
				}});
			return result;
		}
	}
}
