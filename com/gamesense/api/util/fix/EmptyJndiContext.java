/*
 * Decompiled with CFR 0.152.
 */
package com.gamesense.api.util.fix;

import java.util.Hashtable;
import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NameClassPair;
import javax.naming.NameParser;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public enum EmptyJndiContext implements Context,
DirContext
{
    INSTANCE;


    @Override
    public Object lookup(Name name) {
        return null;
    }

    @Override
    public Object lookup(String name) {
        return null;
    }

    @Override
    public void bind(Name name, Object obj) {
    }

    @Override
    public void bind(String name, Object obj) {
    }

    @Override
    public void rebind(Name name, Object obj) {
    }

    @Override
    public void rebind(String name, Object obj) {
    }

    @Override
    public void unbind(Name name) {
    }

    @Override
    public void unbind(String name) {
    }

    @Override
    public void rename(Name oldName, Name newName) {
    }

    @Override
    public void rename(String oldName, String newName) {
    }

    @Override
    public NamingEnumeration<NameClassPair> list(Name name) {
        return null;
    }

    @Override
    public NamingEnumeration<NameClassPair> list(String name) {
        return null;
    }

    @Override
    public NamingEnumeration<Binding> listBindings(Name name) throws NamingException {
        return (NamingEnumeration)EmptyJndiContext.panic();
    }

    @Override
    public NamingEnumeration<Binding> listBindings(String name) throws NamingException {
        return (NamingEnumeration)EmptyJndiContext.panic();
    }

    @Override
    public void destroySubcontext(Name name) {
    }

    @Override
    public void destroySubcontext(String name) {
    }

    @Override
    public Context createSubcontext(Name name) throws NamingException {
        return (Context)EmptyJndiContext.panic();
    }

    @Override
    public Context createSubcontext(String name) throws NamingException {
        return (Context)EmptyJndiContext.panic();
    }

    @Override
    public Object lookupLink(Name name) {
        return null;
    }

    @Override
    public Object lookupLink(String name) {
        return null;
    }

    @Override
    public NameParser getNameParser(Name name) throws NamingException {
        return (NameParser)EmptyJndiContext.panic();
    }

    @Override
    public NameParser getNameParser(String name) throws NamingException {
        return (NameParser)EmptyJndiContext.panic();
    }

    @Override
    public Name composeName(Name name, Name prefix) throws NamingException {
        return (Name)EmptyJndiContext.panic();
    }

    @Override
    public String composeName(String name, String prefix) throws NamingException {
        return (String)EmptyJndiContext.panic();
    }

    @Override
    public Object addToEnvironment(String propName, Object propVal) {
        return null;
    }

    @Override
    public Object removeFromEnvironment(String propName) {
        return null;
    }

    @Override
    public Hashtable<?, ?> getEnvironment() {
        return new Hashtable();
    }

    @Override
    public void close() {
    }

    @Override
    public String getNameInNamespace() {
        return "";
    }

    @Override
    public Attributes getAttributes(Name name) throws NamingException {
        return (Attributes)EmptyJndiContext.panic();
    }

    @Override
    public Attributes getAttributes(String name) throws NamingException {
        return (Attributes)EmptyJndiContext.panic();
    }

    @Override
    public Attributes getAttributes(Name name, String[] attrIds) throws NamingException {
        return (Attributes)EmptyJndiContext.panic();
    }

    @Override
    public Attributes getAttributes(String name, String[] attrIds) throws NamingException {
        return (Attributes)EmptyJndiContext.panic();
    }

    @Override
    public void modifyAttributes(Name name, int mod_op, Attributes attrs) {
    }

    @Override
    public void modifyAttributes(String name, int mod_op, Attributes attrs) {
    }

    @Override
    public void modifyAttributes(Name name, ModificationItem[] mods) {
    }

    @Override
    public void modifyAttributes(String name, ModificationItem[] mods) {
    }

    @Override
    public void bind(Name name, Object obj, Attributes attrs) {
    }

    @Override
    public void bind(String name, Object obj, Attributes attrs) {
    }

    @Override
    public void rebind(Name name, Object obj, Attributes attrs) {
    }

    @Override
    public void rebind(String name, Object obj, Attributes attrs) {
    }

    @Override
    public DirContext createSubcontext(Name name, Attributes attrs) throws NamingException {
        return (DirContext)EmptyJndiContext.panic();
    }

    @Override
    public DirContext createSubcontext(String name, Attributes attrs) throws NamingException {
        return (DirContext)EmptyJndiContext.panic();
    }

    @Override
    public DirContext getSchema(Name name) throws NamingException {
        return (DirContext)EmptyJndiContext.panic();
    }

    @Override
    public DirContext getSchema(String name) throws NamingException {
        return (DirContext)EmptyJndiContext.panic();
    }

    @Override
    public DirContext getSchemaClassDefinition(Name name) throws NamingException {
        return (DirContext)EmptyJndiContext.panic();
    }

    @Override
    public DirContext getSchemaClassDefinition(String name) throws NamingException {
        return (DirContext)EmptyJndiContext.panic();
    }

    @Override
    public NamingEnumeration<SearchResult> search(Name name, Attributes matchingAttributes, String[] attributesToReturn) throws NamingException {
        return (NamingEnumeration)EmptyJndiContext.panic();
    }

    @Override
    public NamingEnumeration<SearchResult> search(String name, Attributes matchingAttributes, String[] attributesToReturn) throws NamingException {
        return (NamingEnumeration)EmptyJndiContext.panic();
    }

    @Override
    public NamingEnumeration<SearchResult> search(Name name, Attributes matchingAttributes) throws NamingException {
        return (NamingEnumeration)EmptyJndiContext.panic();
    }

    @Override
    public NamingEnumeration<SearchResult> search(String name, Attributes matchingAttributes) throws NamingException {
        return (NamingEnumeration)EmptyJndiContext.panic();
    }

    @Override
    public NamingEnumeration<SearchResult> search(Name name, String filter, SearchControls cons) throws NamingException {
        return (NamingEnumeration)EmptyJndiContext.panic();
    }

    @Override
    public NamingEnumeration<SearchResult> search(String name, String filter, SearchControls cons) throws NamingException {
        return (NamingEnumeration)EmptyJndiContext.panic();
    }

    @Override
    public NamingEnumeration<SearchResult> search(Name name, String filterExpr, Object[] filterArgs, SearchControls cons) throws NamingException {
        return (NamingEnumeration)EmptyJndiContext.panic();
    }

    @Override
    public NamingEnumeration<SearchResult> search(String name, String filterExpr, Object[] filterArgs, SearchControls cons) throws NamingException {
        return (NamingEnumeration)EmptyJndiContext.panic();
    }

    private static <T> T panic() throws NamingException {
        throw new NamingException("JNDI has been removed");
    }
}

