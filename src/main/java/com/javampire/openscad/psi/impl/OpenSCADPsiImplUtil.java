package com.javampire.openscad.psi.impl;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.javampire.openscad.OpenSCADIcons;
import com.javampire.openscad.OpenSCADParserDefinition;
import com.javampire.openscad.psi.*;
import com.javampire.openscad.references.OpenSCADFunctionReference;
import com.javampire.openscad.references.OpenSCADModuleReference;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class OpenSCADPsiImplUtil {

    public static ItemPresentation getPresentation(final PsiElement element) {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                if (OpenSCADParserDefinition.NAMED_ELEMENTS.contains(element.getNode().getElementType())) {
                    final ASTNode nameNode = element.getNode().findChildByType(OpenSCADTypes.IDENTIFIER);
                    if (nameNode != null) {
                        return nameNode.getText();
                    }
                } else if (OpenSCADParserDefinition.IMPORT_TOKENS.contains(element.getNode().getElementType())) {
                    final ASTNode pathNode = element.getNode().findChildByType(OpenSCADTypes.INCLUDE_PATH);
                    if (pathNode != null) {
                        return pathNode.getText().replaceAll("^.*/([^/]*)$", "$1");
                    }
                }
                return null;
            }

            @Nullable
            @Override
            public String getLocationString() {
                return element.getContainingFile().getName();
            }

            @Nullable
            @Override
            public Icon getIcon(boolean unused) {
                if (element.getNode().getElementType() == OpenSCADTypes.MODULE_DECLARATION) {
                    return OpenSCADIcons.MODULE;
                }
                if (element.getNode().getElementType() == OpenSCADTypes.FUNCTION_DECLARATION) {
                    return OpenSCADIcons.FUNCTION;
                }
                if (OpenSCADParserDefinition.IMPORT_TOKENS.contains(element.getNode().getElementType())) {
                    return OpenSCADIcons.FILE;
                }
                return null;
            }
        };
    }

    public static String getName(final PsiElement element) {
        if (OpenSCADParserDefinition.NAMED_ELEMENTS.contains(element.getNode().getElementType())) {
            final ASTNode nameNode = element.getNode().findChildByType(OpenSCADTypes.IDENTIFIER);
            if (nameNode != null) {
                return nameNode.getText();
            }
        } else if (element.getNode().getElementType() == OpenSCADTypes.REF_EXPR) {
            final ASTNode nameNode = element.getNode().getLastChildNode();
            if (nameNode != null && nameNode.getElementType() == OpenSCADTypes.IDENTIFIER) {
                return nameNode.getText();
            }
        }
        return null;
    }

    public static PsiElement setName(PsiElement element, String newName) {
        if (OpenSCADParserDefinition.NAMED_ELEMENTS.contains(element.getNode().getElementType())) {
            final ASTNode nameNode = element.getNode().findChildByType(OpenSCADTypes.IDENTIFIER);
            if (nameNode != null) {
                PsiElement newNameElement = OpenSCADElementFactory.createIdentifier(element.getProject(), newName);
                element.getNode().replaceChild(nameNode, newNameElement.getNode());
            }
        } else if (element.getNode().getElementType() == OpenSCADTypes.REF_EXPR) {
            final ASTNode nameNode = element.getNode().getLastChildNode();
            if (nameNode != null && nameNode.getElementType() == OpenSCADTypes.IDENTIFIER) {
                PsiElement newNameElement = OpenSCADElementFactory.createIdentifier(element.getProject(), newName);
                element.getNode().replaceChild(nameNode, newNameElement.getNode());
            }
        }
        return element;
    }

    public static PsiElement getNameIdentifier(PsiElement element) {
        if (OpenSCADParserDefinition.NAMED_ELEMENTS.contains(element.getNode().getElementType())) {
            final ASTNode nameNode = element.getNode().findChildByType(OpenSCADTypes.IDENTIFIER);
            if (nameNode != null) {
                return nameNode.getPsi();
            }
        } else if (element.getNode().getElementType() == OpenSCADTypes.REF_EXPR) {
            final ASTNode nameNode = element.getNode().getLastChildNode();
            if (nameNode != null && nameNode.getElementType() == OpenSCADTypes.IDENTIFIER) {
                return nameNode.getPsi();
            }
        }
        return null;
    }

    public static PsiReference getReference(PsiElement element) {
        // TODO: implement
        PsiReference ref;
        if (element instanceof OpenSCADNamedElement) {
            final String name = ((OpenSCADNamedElement) element).getName();
            if (name != null) {
                System.out.println("getReference(named element): " + name);
                if (element instanceof OpenSCADModuleObjNameRef || element instanceof OpenSCADModuleOpNameRef) {
                    ref = new OpenSCADModuleReference((OpenSCADNamedElement) element, new TextRange(0, element.getTextLength()));
                    System.out.println("Created module ref: " + ref);
                    return ref;
                } else if (element instanceof OpenSCADFunctionNameRef) {
                    ref = new OpenSCADFunctionReference((OpenSCADNamedElement) element, new TextRange(0, element.getTextLength()));
                    System.out.println("Created function ref: " + ref);
                    return ref;
                }
            }
        } else {
            System.out.println(element + "(no getName)");
        }
        return null;
    }

}
