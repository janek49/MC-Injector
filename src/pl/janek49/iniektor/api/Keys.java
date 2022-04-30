package pl.janek49.iniektor.api;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.input.Keyboard;

public enum Keys {

    KEY_NONE(Keyboard.KEY_NONE, GLFW.GLFW_KEY_UNKNOWN),
    KEY_RSHIFT(Keyboard.KEY_RSHIFT, GLFW.GLFW_KEY_RIGHT_SHIFT),
    KEY_LMENU(Keyboard.KEY_LMENU, GLFW.GLFW_KEY_LEFT_ALT),
    KEY_SPACE(Keyboard.KEY_SPACE, GLFW.GLFW_KEY_SPACE),
    KEY_LSHIFT(Keyboard.KEY_LSHIFT, GLFW.GLFW_KEY_LEFT_SHIFT),
    KEY_A(Keyboard.KEY_A, GLFW.GLFW_KEY_A),
    KEY_B(Keyboard.KEY_B, GLFW.GLFW_KEY_B),
    KEY_C(Keyboard.KEY_C, GLFW.GLFW_KEY_C),
    KEY_D(Keyboard.KEY_D, GLFW.GLFW_KEY_D),
    KEY_E(Keyboard.KEY_E, GLFW.GLFW_KEY_E),
    KEY_F(Keyboard.KEY_F, GLFW.GLFW_KEY_F),
    KEY_G(Keyboard.KEY_G, GLFW.GLFW_KEY_G),
    KEY_H(Keyboard.KEY_H, GLFW.GLFW_KEY_H),
    KEY_I(Keyboard.KEY_I, GLFW.GLFW_KEY_I),
    KEY_J(Keyboard.KEY_J, GLFW.GLFW_KEY_J),
    KEY_K(Keyboard.KEY_K, GLFW.GLFW_KEY_K),
    KEY_L(Keyboard.KEY_L, GLFW.GLFW_KEY_L),
    KEY_M(Keyboard.KEY_M, GLFW.GLFW_KEY_M),
    KEY_N(Keyboard.KEY_N, GLFW.GLFW_KEY_N),
    KEY_O(Keyboard.KEY_O, GLFW.GLFW_KEY_O),
    KEY_P(Keyboard.KEY_P, GLFW.GLFW_KEY_P),
    KEY_Q(Keyboard.KEY_Q, GLFW.GLFW_KEY_Q),
    KEY_R(Keyboard.KEY_R, GLFW.GLFW_KEY_R),
    KEY_S(Keyboard.KEY_S, GLFW.GLFW_KEY_S),
    KEY_T(Keyboard.KEY_T, GLFW.GLFW_KEY_T),
    KEY_U(Keyboard.KEY_U, GLFW.GLFW_KEY_U),
    KEY_V(Keyboard.KEY_V, GLFW.GLFW_KEY_V),
    KEY_W(Keyboard.KEY_W, GLFW.GLFW_KEY_W),
    KEY_X(Keyboard.KEY_X, GLFW.GLFW_KEY_X),
    KEY_Y(Keyboard.KEY_Y, GLFW.GLFW_KEY_Y),
    KEY_Z(Keyboard.KEY_Z, GLFW.GLFW_KEY_Z);

    public int gl2code, glfwcode;

    private Keys(int gl2, int glfw) {
        this.gl2code = gl2;
        this.glfwcode = glfw;
    }
}
