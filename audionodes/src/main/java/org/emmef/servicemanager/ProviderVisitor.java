package org.emmef.servicemanager;

import java.util.List;

public interface ProviderVisitor<T> {
	enum VisitState { NONE, SUCCESS, FAILURE }

	VisitState visit(List<T> list);
}
