package vdi.component.db.app

internal const val DefaultPoolSize: UByte = 5u

// vdi_control_*.dataset_meta
const val DatasetMetaMaxNameLength = 1024
const val DatasetMetaMaxShortNameLength = 300
const val DatasetMetaMaxShortAttributionLength = 300
const val DatasetMetaMaxCategoryLength = 100
const val DatasetMetaMaxSummaryFieldLength: Int = 4000 // max size for varchar in oracle

// vdi_control_*.dataset_contact
const val DatasetContactNameMaxLength = 300
const val DatasetContactEmailMaxLength = 4000
const val DatasetContactAffiliationMaxLength = 4000
const val DatasetContactCityMaxLength = 200
const val DatasetContactStateMaxLength = 200
const val DatasetContactCountryMaxLength = 200
const val DatasetContactAddressMaxLength = 1000

// vdi_control_*.dataset_hyperlink
const val DatasetHyperlinkUrlMaxLength = 200
const val DatasetHyperlinkTextMaxLength = 300
const val DatasetHyperlinkDescriptionMaxLength = 4000

// vdi_control_*.dataset_publication
const val DatasetPublicationCitationMaxLength = 2000
const val DatasetPublicationPubMedIDMaxLength = 30

// vdi_control_*.dataset_organism
const val DatasetOrganismAbbrevMaxLength = 20
