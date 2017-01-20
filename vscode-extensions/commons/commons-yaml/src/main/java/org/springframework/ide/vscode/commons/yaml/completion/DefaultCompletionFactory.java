/*******************************************************************************
 * Copyright (c) 2016-2017 Pivotal, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Pivotal, Inc. - initial API and implementation
 *******************************************************************************/

package org.springframework.ide.vscode.commons.yaml.completion;

import org.eclipse.lsp4j.CompletionItemKind;
import org.springframework.ide.vscode.commons.languageserver.completion.DocumentEdits;
import org.springframework.ide.vscode.commons.languageserver.completion.ICompletionProposal;
import org.springframework.ide.vscode.commons.languageserver.completion.ScoreableProposal;
import org.springframework.ide.vscode.commons.util.Renderable;
import org.springframework.ide.vscode.commons.util.text.IDocument;
import org.springframework.ide.vscode.commons.yaml.hover.YPropertyInfoTemplates;
import org.springframework.ide.vscode.commons.yaml.schema.YType;
import org.springframework.ide.vscode.commons.yaml.schema.YTypeUtil;
import org.springframework.ide.vscode.commons.yaml.schema.YTypedProperty;

public class DefaultCompletionFactory implements CompletionFactory {
	
	private static final int ERROR_COMPLETION_SCORE = -10000000;

	public static class BeanPropertyProposal extends ScoreableProposal {

		private IDocument doc;
		private String contextProperty;
		private YType contextType;
		private String query;
		private YTypedProperty p;
		private double baseScore;
		private DocumentEdits edits;
		private YTypeUtil typeUtil;

		public BeanPropertyProposal(IDocument doc, String contextProperty, YType contextType, String query, YTypedProperty p, double score, DocumentEdits edits, YTypeUtil typeUtil) {
			super();
			this.doc = doc;
			this.contextProperty = contextProperty;
			this.contextType = contextType;
			this.query = query;
			this.p = p;
			this.baseScore = score;
			this.edits = edits;
			this.typeUtil = typeUtil;
		}
		
		@Override
		public double getBaseScore() {
			return baseScore;
		}

		@Override
		public String getLabel() {
			return p.getName();
		}

		@Override
		public CompletionItemKind getKind() {
			return CompletionItemKind.Field;
		}

		@Override
		public DocumentEdits getTextEdit() {
			return edits;
		}

		@Override
		public String getDetail() {
			return typeUtil.niceTypeName(p.getType());
		}

		@Override
		public Renderable getDocumentation() {
			return YPropertyInfoTemplates.createCompletionDocumentation(contextProperty, contextType, p);
		}
	}
	
	public class ValueProposal extends ScoreableProposal {

		private String value;
		private String query;
		private String label;
		private YType type;
		private double baseScore;
		private DocumentEdits edits;
		private YTypeUtil typeUtil;

		public ValueProposal(String value, String query, String label, YType type, double score, DocumentEdits edits, YTypeUtil typeUtil) {
			this.value = value;
			this.query = query;
			this.label = label;
			this.type = type;
			this.baseScore = score;
			this.edits = edits;
			this.typeUtil = typeUtil;
		}

		@Override
		public double getBaseScore() {
			return baseScore;
		}

		@Override
		public String getLabel() {
			return label;
		}

		@Override
		public CompletionItemKind getKind() {
			return CompletionItemKind.Keyword;
		}

		@Override
		public DocumentEdits getTextEdit() {
			return edits;
		}
		
		@Override
		public String toString() {
			return "ValueProposal("+value+")";
		}

		@Override
		public String getDetail() {
			return typeUtil.niceTypeName(type);
		}

		@Override
		public Renderable getDocumentation() {
			return null;
		}
	}
	
	public static final class ErrorProposal extends ScoreableProposal {
		private final String label;
		private DocumentEdits edits;
		private YTypeUtil typeUtil;
		private YType type;

		public ErrorProposal(String label, YType type, DocumentEdits edits, YTypeUtil typeUtil) {
			this.label = label;
			this.edits = edits;
			this.typeUtil = typeUtil;
			this.type = type;
		}

		@Override
		public DocumentEdits getTextEdit() {
			return edits;
		}

		@Override
		public String getLabel() {
			return label;
		}

		@Override
		public CompletionItemKind getKind() {
			return CompletionItemKind.Value;
		}

		@Override
		public Renderable getDocumentation() {
			return null;
		}

		@Override
		public double getBaseScore() {
			return ERROR_COMPLETION_SCORE;
		}

		@Override
		public String toString() {
			return "ErrorProposal()";
		}

		@Override
		public String getDetail() {
			return typeUtil.niceTypeName(type);
		}
	}

	@Override
	public ICompletionProposal beanProperty(IDocument doc, String contextProperty, YType contextType, String query, YTypedProperty p, double score, DocumentEdits edits, YTypeUtil typeUtil) {
		return new BeanPropertyProposal(doc, contextProperty, contextType, query, p, score, edits, typeUtil);
	}

	@Override
	public ICompletionProposal valueProposal(String value, String query, String label, YType type, double score, DocumentEdits edits, YTypeUtil typeUtil) {
		return new ValueProposal(value, query, label, type, score, edits, typeUtil);
	}

	@Override
	public ICompletionProposal errorMessage(String message, String query, YType type, DocumentEdits edits, YTypeUtil typeUtil) {
		final String label = message;
		return new ErrorProposal(label, type, edits, typeUtil);
	}
}
