package com.derpderphurr.morse;

import java.util.List;

public class CodeCharacter {
    public final int index;
    public final String cha;
    public final List<CodeParticle> particles;

    public CodeCharacter(int index, String cha, List<CodeParticle> ditdah) {
        this.index = index;
        this.cha = cha;
        this.particles = ditdah;
    }

    public int getIndex() {
        return index;
    }

    public String getCha() {
        return cha;
    }

    public List<CodeParticle> getParticles() {
        return particles;
    }
}
