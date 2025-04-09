package vdi.component.db.app

internal const val DefaultPoolSize: UByte = 5u

// vdi_control_*.dataset_meta


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
