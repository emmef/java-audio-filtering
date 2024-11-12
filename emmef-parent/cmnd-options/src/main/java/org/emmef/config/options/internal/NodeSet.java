package org.emmef.config.options.internal;

import java.util.Collections;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

import org.emmef.config.options.internal.ParseNode.Mandatory;

public class NodeSet {
	public static Comparator<ParseNode> NODE_PARSE_COMPARATOR = new Comparator<ParseNode>() {
		@Override
		public int compare(ParseNode o1, ParseNode o2) {
			if (o1.isBroadMatch()) {
				if (!o2.isBroadMatch()) {
					return 1;
				}
			}
			else if (o2.isBroadMatch()) {
				return -1;
			}
			final int compare = ParseNode.MANDATORY_COMPARATOR.compare(o1.getMandatory(), o2.getMandatory());
			if (compare != 0) {
				return compare;
			}
			
			return o1.getNumber() - o2.getNumber();
		}
	};
	
	public static Comparator<ParseNode> NODE_DISPLAY_COMPARATOR = new Comparator<ParseNode>() {
		@Override
		public int compare(ParseNode o1, ParseNode o2) {
			final int compare = ParseNode.MANDATORY_COMPARATOR.compare(o1.getMandatory(), o2.getMandatory());
			if (compare != 0) {
				return compare;
			}
			
			if (o1.isBroadMatch()) {
				if (!o2.isBroadMatch()) {
					return 1;
				}
			}
			else if (o2.isBroadMatch()) {
				return -1;
			}
			
			return o1.getNumber() - o2.getNumber();
		}
	};
	
	private final SortedSet<ParseNode> nodes = new TreeSet<ParseNode>(NODE_PARSE_COMPARATOR);
	private final SortedSet<ParseNode> publishedNodes = Collections.unmodifiableSortedSet(nodes);
	private final SortedSet<ParseNode> displayNodes = new TreeSet<ParseNode>(NODE_DISPLAY_COMPARATOR);
	private final SortedSet<ParseNode> publishedDisplayNodes = Collections.unmodifiableSortedSet(displayNodes);

	public <T extends ParseNode> T add(T node) {
		if (nodes.add(node)) {
			displayNodes.add(node);
			return node;
		}
		throw new IllegalStateException("Couldn't add node");
	}

	public ParseNode findMatch(String argument) {
		for (final ParseNode node : nodes) {
			if ((node.isMultiple() || !node.isHandled()) && node.matches(argument)) {
				return node;
			}
		}
		return null;
	}

	public SortedSet<ParseNode> getDisplayNodes() {
		return publishedDisplayNodes;
	}

	public SortedSet<ParseNode> getParseNodes() {
		return publishedNodes;
	}

	public void checkMandatoryNodes(Object owner) {
		final StringBuilder message = new StringBuilder(); 
		for (ParseNode subNode : displayNodes) {
			if (subNode.getMandatory() != Mandatory.NO && !subNode.isHandled()) {
				message.append(' ').append(subNode);
			}
		}
		if (message.length() > 0) {
			throw new IllegalStateException(owner + " has mandatory values that were not present:" + message);
		}
	}
}
