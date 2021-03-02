package com.hxg.sofa.jraft.rhea;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.hxg.sofa.jraft.rhea.util.Lists;
import com.hxg.sofa.jraft.util.Describer;

  
public final class DescriberManager {

    private static final DescriberManager INSTANCE   = new DescriberManager();

    private final List<Describer>         describers = new CopyOnWriteArrayList<>();

    public static DescriberManager getInstance() {
        return INSTANCE;
    }

    public void addDescriber(final Describer describer) {
        this.describers.add(describer);
    }

    public List<Describer> getAllDescribers() {
        return Lists.newArrayList(this.describers);
    }
}
