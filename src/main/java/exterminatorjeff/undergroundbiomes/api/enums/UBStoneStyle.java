package exterminatorjeff.undergroundbiomes.api.enums;

/**
 * @author LouisDB
 */
public enum UBStoneStyle {
  STONE, COBBLE, BRICK, OVERGROWN, OVERGROWN_SNOWED;

  @Override
  public String toString() {
    return name().toLowerCase();
  }

}
