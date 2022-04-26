package test;

import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.mapper.MojangMapper;

public class TestMojangMapper {
    public static void main(String[] args) {
        MojangMapper mm = new MojangMapper("C:\\Users\\Jan\\IdeaProjects\\MC-Injector\\versions\\1.14.4\\");
      mm.init();

    }
}
