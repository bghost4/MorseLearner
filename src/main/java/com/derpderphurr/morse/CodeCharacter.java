package com.derpderphurr.morse;

import java.util.List;

public class CodeCharacter {
    public final String cha;
    public final List<CodeParticle> particles;

    public CodeCharacter( String cha, List<CodeParticle> ditdah) {
        this.cha = cha;
        this.particles = ditdah;
    }

    public String getCha() {
        return cha;
    }

    public List<CodeParticle> getParticles() {
        return particles;
    }
}
