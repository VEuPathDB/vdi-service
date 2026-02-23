package vdi.service.rest.conversion;

import org.jetbrains.annotations.NotNull;
import vdi.service.rest.generated.model.DatasetCharacteristicsImpl;

public class DatasetCharacteristics extends DatasetCharacteristicsImpl {
  public DatasetCharacteristics(@NotNull vdi.model.meta.DatasetCharacteristics characteristics) {
    setStudyDesign(characteristics.getStudyDesign());
    setStudyType(characteristics.getStudyType());

    if (!characteristics.getCountries().isEmpty())
      setCountries(characteristics.getCountries());

    if (characteristics.getYears() != null)
      setYears(new SampleYearRange(characteristics.getYears()));

    if (!characteristics.getStudySpecies().isEmpty())
      setStudySpecies(characteristics.getStudySpecies());

    
  }
}
