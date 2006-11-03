/** Copyright (c) 2004 Memorial Sloan-Kettering Cancer Center.
 **
 ** Code written by: Ethan Cerami
 ** Authors: Ethan Cerami, Gary Bader, Chris Sander
 **
 ** This library is free software; you can redistribute it and/or modify it
 ** under the terms of the GNU Lesser General Public License as published
 ** by the Free Software Foundation; either version 2.1 of the License, or
 ** any later version.
 ** 
 ** This library is distributed in the hope that it will be useful, but
 ** WITHOUT ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF
 ** MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.  The software and
 ** documentation provided hereunder is on an "as is" basis, and
 ** Memorial Sloan-Kettering Cancer Center 
 ** has no obligations to provide maintenance, support,
 ** updates, enhancements or modifications.  In no event shall
 ** Memorial Sloan-Kettering Cancer Center
 ** be liable to any party for direct, indirect, special,
 ** incidental or consequential damages, including lost profits, arising
 ** out of the use of this software and its documentation, even if
 ** Memorial Sloan-Kettering Cancer Center 
 ** has been advised of the possibility of such damage.  See
 ** the GNU Lesser General Public License for more details.
 ** 
 ** You should have received a copy of the GNU Lesser General Public License
 ** along with this library; if not, write to the Free Software Foundation,
 ** Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.
 **/
package org.cytoscape.coreplugin.psi_mi.data_mapper;

import org.cytoscape.coreplugin.psi_mi.schema.mi1.*;
import org.cytoscape.coreplugin.psi_mi.util.ListUtil;
import org.cytoscape.coreplugin.psi_mi.model.Interactor;
import org.cytoscape.coreplugin.psi_mi.model.AttributeBag;
import org.cytoscape.coreplugin.psi_mi.model.ExternalReference;
import org.cytoscape.coreplugin.psi_mi.model.Interaction;
import org.cytoscape.coreplugin.psi_mi.model.vocab.InteractorVocab;
import org.cytoscape.coreplugin.psi_mi.model.vocab.InteractionVocab;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Converts Data Services Object Model to the PSI-MI Format.
 * <p/>
 * Official version of PSI-MI is available at:
 * http://psidev.sourceforge.net/mi/xml/src/MIF.xsd
 *
 * @author Ethan Cerami
 */
public class MapInteractionsToPsiOne implements Mapper {

    private static final String EXP_AFFINITY_PRECIPITATION =
            "Affinity Precipitation";

    private static final String EXP_AFFINITY_CHROMOTOGRAPHY =
            "Affinity Chromatography";

    private static final String EXP_TWO_HYBRID = "Two Hybrid";

    private static final String EXP_PURIFIED_COMPLEX = "Purified Complex";

    /**
     * Pub Med Database.
     */
    private static final String PUB_MED_DB = "pubmed";

    private EntrySet entrySet;

    /**
     * ArrayList of Protein-Protein Interactions.
     */
    private ArrayList interactions;

    /**
     * Constructor.
     *
     * @param interactions ArrayList of Interactions.
     */
    public MapInteractionsToPsiOne(ArrayList interactions) {
        this.interactions = interactions;
    }

    /**
     * Perform Mapping.
     */
    public void doMapping() {
        // Create Entry Set and Entry
        entrySet = new EntrySet();
        /*entrySet.setLevel(1);
        entrySet.setVersion(1);
        Entry entry = new Entry();

        //  Get Interactor List
        InteractorList interactorList = getInteractorList();

        //  Get Interaction List
        InteractionList interactionList = getInteractionList();

        //  Add to Entry node
        entry.setInteractorList(interactorList);
        entry.setInteractionList(interactionList);
        entrySet.addEntry(entry);   */
        entrySet = ListUtil.getPsiOneEntrySet();

    }

//    /**
//     * Gets PSI XML.
//     *
//     * @return Root PSI Element.
//     */
//    public EntrySet getPsiXml() {
//        return entrySet;
//    }
//
//    /**
//     * Gets Interactor List.
//     *
//     * @return Castor InteractorList.
//     */
//    private InteractorList getInteractorList() {
//        HashMap proteinSet = getNonRedundantInteractors();
//        InteractorList interactorList = new InteractorList();
//
//        //  Iterate through all Interactors
//        Iterator iterator = proteinSet.values().iterator();
//        while (iterator.hasNext()) {
//            //  Create new Interactor
//            Interactor interactor = (Interactor) iterator.next();
//            ProteinInteractorType castorInteractor
//                    = new ProteinInteractorType();
//            setNameId(interactor, castorInteractor);
//            setOrganism(interactor, castorInteractor);
//            setSequence(interactor, castorInteractor);
//            XrefType xref = createExternalRefs(interactor);
//            if (xref != null) {
//                castorInteractor.setXref(xref);
//            }
//
//            //  Add to Interactor List
//            interactorList.addProteinInteractor(castorInteractor);
//        }
//        return interactorList;
//    }
//
//    /**
//     * Sets Sequence Data.
//     */
//    private void setSequence(Interactor interactor, ProteinInteractorType
//            castorInteractor) {
//        String seqData = (String) interactor.getAttribute
//                (InteractorVocab.SEQUENCE_DATA);
//        if (seqData != null) {
//            castorInteractor.setSequence(seqData);
//        }
//    }
//
//    /**
//     * Sets Interactor Name and ID.
//     *
//     * @param interactor       Data Services Interactor object.
//     * @param castorInteractor Castor Protein Interactor Object.
//     */
//    private void setNameId(Interactor interactor, ProteinInteractorType
//            castorInteractor) {
//        NamesType names = new NamesType();
//        names.setShortLabel(interactor.getName());
//        String fullName = (String) interactor.getAttribute
//                (InteractorVocab.FULL_NAME);
//        if (fullName != null) {
//            names.setFullName(fullName);
//        }
//        castorInteractor.setNames(names);
//        castorInteractor.setId(interactor.getName());
//    }
//
//    /**
//     * Sets Interactor Organism.
//     *
//     * @param interactor       Data Services Interactor Object.
//     * @param castorInteractor Castor Protein Interactor Object.
//     */
//    private void setOrganism(Interactor interactor,
//            ProteinInteractorType castorInteractor) {
//        ProteinInteractorType.Organism organism = new ProteinInteractorType.Organism();
//        String taxonomyID = (String) interactor.getAttribute
//                (InteractorVocab.ORGANISM_NCBI_TAXONOMY_ID);
//        if (taxonomyID != null) {
//            int taxID = Integer.parseInt(taxonomyID);
//            organism.setNcbiTaxId(taxID);
//        }
//
//        NamesType orgNames = new NamesType();
//        String commonName = (String) interactor.getAttribute
//                (InteractorVocab.ORGANISM_COMMON_NAME);
//        if (commonName != null) {
//            orgNames.setShortLabel(commonName);
//        }
//
//        String speciesName = (String) interactor.getAttribute
//                (InteractorVocab.ORGANISM_SPECIES_NAME);
//        if (speciesName != null) {
//            orgNames.setFullName(speciesName);
//        }
//        organism.setNames(orgNames);
//        castorInteractor.setOrganism(organism);
//    }
//
//    /**
//     * Sets Interactor External References.
//     * Filters out any redundant external references.
//     */
//    private XrefType createExternalRefs(AttributeBag bag) {
//        HashSet set = new HashSet();
//        ExternalReference refs [] = bag.getExternalRefs();
//        XrefType xref = new XrefType();
//
//        if (refs != null && refs.length > 0) {
//            //  Add Primary Reference
//            createPrimaryKey(refs[0], xref);
//
//            //  All others become Secondary References
//            if (refs.length > 1) {
//                for (int i = 1; i < refs.length; i++) {
//                    String key = this.generateXRefKey(refs[i]);
//                    if (!set.contains(key)) {
//                        createSecondaryKey(refs[i], xref);
//                        set.add(key);
//                    }
//                }
//            }
//        }
//        if (xref.getPrimaryRef() != null) {
//            return xref;
//        } else {
//            return null;
//        }
//    }
//
//    /**
//     * Generates XRef Key.
//     *
//     * @param ref External Reference
//     * @return Hash Key.
//     */
//    private String generateXRefKey(ExternalReference ref) {
//        String key = ref.getDatabase() + "." + ref.getId();
//        return key;
//    }
//
//    /**
//     * Creates Primary Key.
//     *
//     * @param ref  External Reference.
//     * @param xref Castor XRef.
//     */
//    private void createPrimaryKey(ExternalReference ref, XrefType xref) {
//        DbReferenceType primaryRef = new DbReferenceType();
//        primaryRef.setDb(ref.getDatabase());
//        primaryRef.setId(ref.getId());
//        xref.setPrimaryRef(primaryRef);
//    }
//
//    /**
//     * Creates Secondary Key.
//     *
//     * @param ref  External Reference
//     * @param xref Castro XRef.
//     */
//    private void createSecondaryKey(ExternalReference ref, XrefType xref) {
//        DbReferenceType secondaryRef = new DbReferenceType();
//        secondaryRef.setDb(ref.getDatabase());
//        secondaryRef.setId(ref.getId());
//        xref.addSecondaryRef(secondaryRef);
//    }
//
//    /**
//     * Gets a complete list of NonRedundant Proteins.
//     *
//     * @return HashMap of NonRedundant Proteins.
//     */
//    private HashMap getNonRedundantInteractors() {
//        HashMap interactorMap = new HashMap();
//        for (int i = 0; i < interactions.size(); i++) {
//            Interaction interaction = (Interaction) interactions.get(i);
//            ArrayList interactors = interaction.getInteractors();
//            for (int j = 0; j < interactors.size(); j++) {
//                Interactor interactor = (Interactor) interactors.get(j);
//                addToHashMap(interactor, interactorMap);
//            }
//        }
//        return interactorMap;
//    }
//
//    /**
//     * Conditionally adds Protein to HashMap.
//     *
//     * @param interactor    Interactor Object.
//     * @param interactorMap HashMap of NonRedundant Interactors.
//     */
//    private void addToHashMap(Interactor interactor, HashMap interactorMap) {
//        String orfName = interactor.getName();
//        if (!interactorMap.containsKey(orfName)) {
//            interactorMap.put(orfName, interactor);
//        }
//    }
//
//    /**
//     * Gets Interaction List.
//     *
//     * @return Castor InteractionList.
//     */
//    private InteractionList getInteractionList() {
//        InteractionList interactionList = new InteractionList();
//        //  Iterate through all interactions
//        for (int i = 0; i < interactions.size(); i++) {
//
//            //  Create New Interaction
//            InteractionElementType castorInteraction =
//                    new InteractionElementType();
//            Interaction interaction = (Interaction) interactions.get(i);
//
//            //  Add Experiment List
//            ExperimentList expList = getExperimentDescription(interaction, i);
//            castorInteraction.setExperimentList(expList);
//
//            //  Add Participants
//            ParticipantList participantList = getParticipantList(interaction);
//            castorInteraction.setParticipantList(participantList);
//
//            //  Add to Interaction List
//            interactionList.addInteraction(castorInteraction);
//
//            //  Add Xrefs
//            XrefType xref = createExternalRefs(interaction);
//            if (xref != null) {
//                castorInteraction.setXref(xref);
//            }
//        }
//        return interactionList;
//    }
//
//    /**
//     * Gets Experiment Description.
//     *
//     * @param interaction Interaction object.
//     * @return Castor InteractionElementTypeChoice object.
//     */
//    private ExperimentList getExperimentDescription
//            (Interaction interaction, int index) {
//        //  Create New Experiment List
//        ExperimentList expList = new ExperimentList();
//
//        //  Create New Experiment Description
//        ExperimentListItem expItem = new ExperimentListItem();
//        ExperimentType expDescription = new ExperimentType();
//        expItem.setExperimentDescription(expDescription);
//
//        //  Set Experimental ID
//        expDescription.setId("exp" + index);
//
//        //  Set Bibliographic Reference
//        BibrefType bibRef = null;
//
//        Object pmid = interaction.getAttribute(InteractionVocab.PUB_MED_ID);
//        if (pmid != null && pmid instanceof String) {
//            bibRef = createBibRef(PUB_MED_DB, (String) pmid);
//            expDescription.setBibref(bibRef);
//        }
//
//        //  Set Interaction Detection
//        CvType interactionDetection =
//                getInteractionDetection(interaction);
//        expDescription.setInteractionDetection(interactionDetection);
//
//        //  Set Choice Element
//        expList.addExperimentListItem(expItem);
//        return expList;
//    }
//
//    /**
//     * Creates a Bibliography Reference.
//     *
//     * @param database Database.
//     * @param id       ID String.
//     * @return Castor Bibref Object.
//     */
//    private BibrefType createBibRef(String database, String id) {
//        XrefType xref = createXRef(database, id);
//        BibrefType bibRef = new BibrefType();
//        bibRef.setXref(xref);
//        return bibRef;
//    }
//
//    /**
//     * Creates a Primary Reference.
//     *
//     * @param database Database.
//     * @param id       ID String.
//     * @return Castor XRef object
//     */
//    private XrefType createXRef(String database, String id) {
//        XrefType xref = new XrefType();
//        DbReferenceType primaryRef = new DbReferenceType();
//        primaryRef.setDb(database);
//        primaryRef.setId(id);
//        xref.setPrimaryRef(primaryRef);
//        return xref;
//    }
//
//    /**
//     * Gets Interaction Detection element.
//     * It is possible that an interaction is missing important attributes,
//     * such as Experimental System Name, XRef DB, and XRef ID.  All of these
//     * attributes are required by PSI.  Rather than throwing an exception
//     * here, the data_mapper manually specifies "Not Specified" for all missing
//     * attributes.
//     *
//     * @param interaction Interaction.
//     * @return InteractionDetection Object.
//     */
//    private CvType getInteractionDetection
//            (Interaction interaction) {
//        CvType interactionDetection = new CvType();
//        String idStr = null;
//        try {
//            idStr = (String) interaction.getAttribute
//                (InteractionVocab.EXPERIMENTAL_SYSTEM_NAME);
//        } catch (ClassCastException e) {
//            idStr = null;
//        }
//
//        if (idStr == null) {
//            idStr = "Not Specified";
//        }
//
//        String idRef = null;
//        try {
//            idRef = (String) interaction.getAttribute
//                    (InteractionVocab.EXPERIMENTAL_SYSTEM_XREF_ID);
//        } catch (ClassCastException e) {
//            idRef = null;
//        }
//
//        //  If there is no ID Ref, find a best match.
//        if (idRef == null) {
//            if (idStr.equals(EXP_AFFINITY_PRECIPITATION)
//                    || idStr.equals(EXP_AFFINITY_CHROMOTOGRAPHY)) {
//                idStr = "affinity chromatography technologies";
//                idRef = "MI:0004";
//            } else if (idStr.equals(EXP_TWO_HYBRID)) {
//                idStr = "classical two hybrid";
//                idRef = "MI:0018";
//            } else if (idStr.equals(EXP_PURIFIED_COMPLEX)) {
//                idStr = "copurification";
//                idRef = "MI:0025";
//            } else {
//                idRef = "Not Specified";
//            }
//        }
//        NamesType names = createName(idStr, null);
//        interactionDetection.setNames(names);
//
//        String dbRef = null;
//        try {
//            dbRef = (String) interaction.getAttribute
//                (InteractionVocab.EXPERIMENTAL_SYSTEM_XREF_DB);
//        } catch (ClassCastException e) {
//            dbRef = null;
//        }
//        if (dbRef == null) {
//            dbRef = "PSI-MI";
//        }
//
//        XrefType xref = createXRef(dbRef, idRef);
//        interactionDetection.setXref(xref);
//        return interactionDetection;
//    }
//
//    /**
//     * Creates a new Names Object.
//     *
//     * @param shortLabel Short Name Label.
//     * @param fullName   Full Name/Description.
//     * @return Castor Names Object.
//     */
//    private NamesType createName(String shortLabel, String fullName) {
//        NamesType names = new NamesType();
//        names.setShortLabel(shortLabel);
//        if (fullName != null) {
//            names.setFullName(fullName);
//        }
//        return names;
//    }
//
//    /**
//     * Gets the Interaction Participant List.
//     *
//     * @param interaction Interaction object.
//     * @return Castor Participant List.
//     */
//    private InteractionElementType.ParticipantList getParticipantList(Interaction interaction) {
//        InteractionElementType.ParticipantList participantList =
//                new InteractionElementType.ParticipantList();
//
//        ArrayList interactors = interaction.getInteractors();
//
//        for (int i = 0; i < interactors.size(); i++) {
//            Interactor interactor = (Interactor) interactors.get(i);
//            String name = interactor.getName();
//            ProteinParticipantType participant1 = createParticipant(name);
//            participantList.addProteinParticipant(participant1);
//        }
//        return participantList;
//    }
//
//    /**
//     * Create New Protein Participant.
//     *
//     * @param id Protein ID.
//     * @return Castor Protein Participant Object.
//     */
//    private ProteinParticipantType createParticipant(String id) {
//        ProteinParticipantType participant = new ProteinParticipantType();
//        ProteinParticipantTypeChoice choice =
//                new ProteinParticipantTypeChoice();
//        RefType ref = new RefType();
//        ref.setRef(id);
//        choice.setProteinInteractorRef(ref);
//        participant.setProteinParticipantTypeChoice(choice);
//        return participant;
//    }
}