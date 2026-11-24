# Data on the test driver interface

The test interface expects certain input parameters to be supplied as objects linked to data
profiles described later in this document:

* EU country profile, referenced by `baseParameter.euCountryCode`,
* Identity Assertion profile, referenced by
  `baseParameter.idaAssertionProfile.assertionProperties.profileName`,
* TRC Assertion profile, referenced by `trcAssertionProfile.assertionProperties.profileName`,

A data profile acts as a template, providing pre-configured defaults for communication with the
German NCPeH service. This helps keep test interface requests concise, as callers only need to
supply values that override or extend those defaults. A profile also defines infrastructure data
which cannot be supplied per-request, most importantly, the references to the certificates used in
signing IdA or TRC assertions.

Data profiles mandated by gematik or involved service providers in Germany must be coordinated with
the operator of the NCPeH test interface. These jointly defined profiles are to be made available
through the simulator’s test driver interface.

## Object `euCountryCode`

The `euCountryCode` object references the following data:

* The TLS test certificate to be used by the simulator, matching the EU country code
* The homeCommunityId to be used by the simulator, matching the EU country code

The expected country configuration in the NCPeH-FD is based on defined and agreed-upon entries
in the German health professional registry (VZD) and requires a matching configuration in the
NCPeH-FD instance.

### NCPeH country configuration currently used by gematik test cases

The following countries are currently used by gematik:

| Country name   | Assigned eHDSI scenario |
|----------------|-------------------------|
| Belgium (BE)   | ePeD-A                  |
| Bulgaria (BG)  | PS-A                    |
| France (FR)    | ePeD-A                  |
| Latvia (LV)    | ePeD-A, PS-A            |
| Lithuania (LT) | ePeD-A                  |

A test instance of the German NCPeH service needs to be configured for each country.
This requires the following entries in the ALLOWEDLIST_NCPeH_COUNTRY-B:

* The homeCommunityId for the country, as used by the simulator
* The assigned eHDSI scenarios
  Furthermore, the TLS Certificate used by the simulator to represent the country needs to be
  trusted
  by the test instance of the German NCPeH service.

## Object `idaAssertionProfile`

The `idaAssertionProfile` defines a pre-filled data template for an **Identity Assertion (IdA)**,
which proves the identity of a health professional and provides information about both the person
and the organisation they work for. Additional data, such as a country-specific certificate, is
linked to the profile so that a complete, usable assertion can be constructed from the template.
A specific profile is addressed by its `idaProfileName`.

> [!NOTE]
> Depending on the security rules enforced by the NCPeH-FD, it may or may not be
> necessary to reuse the same IdA throughout a workflow for a given patient.

### General IdA profile content

* Name of the health professional
* `purposeOfUse` is `TREATMENT`
    * Can be overridden in a request to the test interface
      (`baseParameter.idaAssertionProfile.assertionProperties.purposeOfUse`)
* The default validity window of a generated IdA is −30 minutes (`NotBefore`) to +30 minutes
  (`NotOnOrAfter`) relative to the time of IdA generation/signing
    * The validity timestamps can be overridden in the test interface request
      (`baseParameter.idaAssertionProfile.assertionProperties.deltaNotBefore` and/or
      `baseParameter.idaAssertionProfile.assertionProperties.deltaNotAfter`)
* Additional profile data consistent with the health professional's identity:
    * `NameId` (health professional identifier; may also be reused by the TRC)
    * Further attributes, e.g.
        * `urn:oasis:names:tc:xspa:1.0:subject:organization`
        * `urn:oasis:names:tc:xspa:1.0:subject:organization-id`
        * `urn:ehdsi:names:subject:healthcare-facility-type`
        * `urn:oasis:names:tc:xspa:1.0:environment:locality`
        * `urn:ehdsi:names:subject:clinical-speciality`
    * Some attributes can be overridden in the request
      (`baseParameter.idaAssertionProfile.assertionProperties.attributeStatements`)
* Any required data not explicitly listed here must also be included in the template

### Specific IdA profile content for the pharmacist role

* The role is set to pharmacist
    * Can be overridden in the request
      (`baseParameter.idaAssertionProfile.structuralRole`)
* Contains the appropriate permission codes for a pharmacist to access ePrescriptions and send
  eDispensations (see
  [eHDSI_SAML_Profile#2.5 Permission Codes](https://webgate.ec.europa.eu/fpfis/wikis/display/EHDSI/SAML+Profile)):
    * Patient Identification
    * Review Existing Orders
    * Review Patient Medications
    * Record Medication Administration Record
    * No other permissions
* Permissions can be overridden in the request either by supplying a list of permissions that
  replaces the template data or by supplying an empty list, in which case the
  `urn:oasis:names:tc:xspa:1.0:subject:hl7:permission` attribute is to be omitted from the IdA
  assertion. (see`baseParameter.idaAssertionProfile.permissions`)

### Specific IdA profile content for the medical doctor role

* The role is set to medical doctor
    * Can be overridden in the request
      (`baseParameter.idaAssertionProfile.structuralRole`)
* Contains the appropriate permission codes for a medical doctor to access a patient summary:
    * Patient Identification
    * Review Vital Signs/Patient Measurements
    * Review Patient Medications
    * Review Medical History
    * Review Problem List
    * No other permissions
* Permissions can be overridden in the request either by supplying a list of permissions that
  replaces the template data or by supplying an empty list, in which case the
  `urn:oasis:names:tc:xspa:1.0:subject:hl7:permission` attribute is to be omitted from the IdA
  assertion. (see`baseParameter.idaAssertionProfile.permissions`)

### Dynamically generated assertion data

* IdA identifier (`Security/Assertion@ID`)
  Note: The TRC uses this ID to link itself to the IdA (via
  `Security/Assertion/Advice/AssertionIdRef`)
* Signature of the assertion
    * Signature invalidation can be requested via the test interface
      (`baseParameter.idaAssertionProfile.assertionProperties.invalidSignature`)

### Defined IdA assertion profiles

The following `idaAssertionProfile` entries are currently defined for gematik test cases:

| idaProfileName          | Name of the health professional                      | Professional role | Assigned country |
|-------------------------|------------------------------------------------------|-------------------|------------------|
| idaPauwels              | Dr. Elisabeth Pauwels                                | pharmacist        | Belgium (BE)     |
| idaPauwelsCountryFrance | **Dr. Elisabeth Pauwels**                            | pharmacist        | **France (FR)**  |
| idaLiepins              | Jānis Liepiņš<br/>UTF8: J\uc481nis Liepi\uc586\uc5a1 | pharmacist        | Latvia (LV)      |
| idaMichailow            | Asparuhk Michailow                                   | pharmacist        | Bulgaria (BG)    |
| idaZilute               | Urte Žiliūtė<br/>UTF8: Urte \uc5bdili\uc6abt\uc497   | pharmacist        | Lithuania (LT)   |

IdAs generated from a profile must be signed with a certificate matching the assigned
country.

> [!NOTE]
> The idaProfile `idaPauwelsCountryFrance` shall exactly contain all data as defined for `idaPauwels` 
> with the exception, that the certificate used to sign the assertion, must be for the country France.

The test instance of the German NCPeH service must be configured to trust the IdA signer
certificates associated with all countries emulated by the Country B simulation.

## Object `trcAssertionProfile`

The `trcAssertionProfile` defines a pre-filled template for a **Treatment Relationship
Confirmation (TRC) Assertion**, which attests the existence of a treatment relationship between
a patient and a Health Care Provider Organisation (HCPO). It provides information about the
context of a specific treatment scenario. Additional data, such as a country-specific
certificate, is linked to the profile to enable construction of a complete, usable assertion
from the template. A specific profile is addressed by its `trcProfileName`.

> [!NOTE]
> The eHDSI SAML profile specifies two links between IdA and TRC:
> * `Security/Assertion/Advice/AssertionIdRef` in the TRC contains the IdA identifier
    (`Security/Assertion@ID`)
> * The `NameId` in the TRC must match the one in the referenced IdA
>
> However, the eHDSI SAML profile does **not** require the IdA and TRC signer certificates to
> be the same.

### General TRC profile content

* `purposeOfUse` is `TREATMENT`
    * Can be overridden in the request
      (`trcAssertionProfile.assertionProperties.purposeOfUse`)
* The `NameId` content must match the linked IdA assertion profile
    * Specific TRC profiles may be defined with intentionally deviating content
* The TRC assertion must be signed with a signer certificate assigned to an EU country. The
  country assignment is part of the TRC profile definition.
* Any required data not explicitly listed here must also be included in the template.

### Dynamically generated/populated TRC assertion data

* TRC identifier (`Security/Assertion@ID`)
* `Security/Assertion/Advice/AssertionIdRef` must contain the assertion ID of the IdA assertion
  being sent together with the TRC assertion
* The default validity window of a generated TRC is −30 minutes (`NotBefore`) to +30 minutes
  (`NotOnOrAfter`) relative to the time of TRC generation/signing
    * The validity timestamps can be overridden in the request
      (`trcAssertionProfile.assertionProperties.deltaNotBefore` and/or
      `trcAssertionProfile.assertionProperties.deltaNotAfter`)
* The `AttributeValue` for `urn:oasis:names:tc:xspa:1.0:subject:subject-id` must be constructed
  from the `patientId` parameter in the test interface request
    * This constructed value can be overridden by `trcAssertionProfile.subjectId` (which is
      expected to be a fully prepared string in that case)
* Signature of the assertion
    * Signature invalidation can be requested via the test interface
      (`trcAssertionProfile.assertionProperties.invalidSignature`)

### Defined TRC assertion profiles

The following `trcAssertionProfile` entries are currently defined for gematik test cases:

| trcProfileName                 | Related idaAssertionProfile | NameId                                                                         | Assigned country |
|--------------------------------|-----------------------------|--------------------------------------------------------------------------------|------------------|
| trcPauwels                     | idaPauwels                  | same as in the related idaAssertionProfile                                     | Belgium (BE)     |
| trcPauwelsNameIdMismatch       | idaPauwels                  | **value string is not equal to NameId of idaAssertionProfile**                 | Belgium (BE)     |
| trcPauwelsNameIdFormatMismatch | idaPauwels                  | **format string is valid, but not equal to NameId<br/>of idaAssertionProfile** | Belgium (BE)     |
| trcPauwelsCountryFrance        | **idaPauwelsCountryFrance** | same as in the related idaAssertionProfile                                     | **France (FR)**  |
| trcLiepins                     | idaLiepins                  | same as in the related idaAssertionProfile                                     | Latvia (LV)      |
| trcMichailow                   | idaMichailow                | same as in the related idaAssertionProfile                                     | Bulgaria (BG)    |
| trcZilute                      | idaZilute                   | same as in the related idaAssertionProfile                                     | Lithuania (LT)   |

TRC assertions generated from profiles must be signed with a certificate matching the
assigned country. The TRC signer certificate may be the same as in the related
`idaAssertionProfile`.

> [!NOTE]
> The trcProfile `trcPauwelsCountryFrance` shall exactly contain all data as defined for `trcPauwels`
> with the exception, that the certificate used to sign the assertion, must be for the country France.

The test instance of the German NCPeH service must be configured to trust the TRC signer
certificates associated with all countries emulated by the Country B simulation.

## Dispensation document construction (ePeD-A only)

Each dispensation document must be prepared based on a single document template, using a dummy medication information.
To complete the template document, the `prescriptionId` is provided (`dispensations[x].prescriptionId`).

Additionally, depending on the intended test data content, the dispensation document shall indicate, whether it contains 
information about medication substitution or not (`dispensation[x].isSubstituted`); 
see [CDA entry template 'eHDSI supply'](https://art-decor.ehdsi.eu/publication/epsos-html-20240422T073854/tmp-1.3.6.1.4.1.12559.11.10.1.3.1.3.3-2023-04-20T134250.html), section 'hl7:entryRelationship' for Substitution.
