package com.nic.nerie.mt_userlogin.service;

import com.nic.nerie.m_designations.service.M_DesignationsService;
import com.nic.nerie.m_offices.model.M_Offices;
import com.nic.nerie.m_phases.model.M_Phases;
import com.nic.nerie.m_processes.repository.M_ProcessesRepository;
import com.nic.nerie.mt_userlogin.model.MT_Userlogin;
import com.nic.nerie.mt_userlogin.repository.MT_UserloginRepository;
import com.nic.nerie.mt_userloginrole.model.MT_UserloginRole;
import com.nic.nerie.mt_userloginrole.repository.MT_UserloginRoleRepository;
import com.nic.nerie.mt_userloginrole.service.MT_UserloginRoleService;
import com.nic.nerie.t_participants.model.T_Participants;
import com.nic.nerie.t_participants.repository.T_ParticipantsRepository;
import com.nic.nerie.utils.SHA256Util;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

@Service
@Validated
public class MT_UserloginService {
    private final MT_UserloginRepository mtUserloginRepository;
    private final MT_UserloginRoleRepository roleRepository;
    private final M_ProcessesRepository mProcessesRepository;
    private final MT_UserloginRoleService mtUserloginRoleService;
    private final PasswordEncoder passwordEncoder;
    private final M_DesignationsService mDesignationsService;
    private final T_ParticipantsRepository tParticipantsRepository;

    private static final Logger dataPersistenceLogger = LoggerFactory.getLogger("DATA_PERSISTENCE_LOGGER");



    @Autowired
    public MT_UserloginService(MT_UserloginRepository mtUserloginRepository,
                               MT_UserloginRoleRepository roleRepository,
                               MT_UserloginRoleService mtUserloginRoleService,
                               PasswordEncoder passwordEncoder,
                               M_ProcessesRepository mProcessesRepository, M_DesignationsService mDesignationsService, T_ParticipantsRepository tParticipantsRepository) {
        this.mtUserloginRepository = mtUserloginRepository;
        this.roleRepository = roleRepository;
        this.mtUserloginRoleService = mtUserloginRoleService;
        this.mProcessesRepository = mProcessesRepository;
        this.passwordEncoder = passwordEncoder;
        this.mDesignationsService = mDesignationsService;
        this.tParticipantsRepository = tParticipantsRepository;
    }

    /**
     * Retrieves the currently authenticated user directly from the Spring Security context.
     * <p>
     * This method verifies that an active authentication exists, the user is fully
     * authenticated, and is not the default guest user ("anonymousUser"). It then
     * queries the database to return the corresponding user entity.
     *
     * @return the {@link MT_Userlogin} entity associated with the current session.
     * @throws AuthenticationCredentialsNotFoundException if no active authentication is found,
     * the user is anonymous, or the user record no longer exists in the database.
     */
    @Transactional(readOnly = true)
    public MT_Userlogin getUserloginFromAuthentication() throws AuthenticationCredentialsNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Ensure authentication exists, is authenticated, and is NOT the default guest user
        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {

            Optional<MT_Userlogin> userloginOptional = mtUserloginRepository.findByUserId(authentication.getName());

            // Check if a value is present, NOT if the Optional is null
            if (userloginOptional.isPresent()) {
                return userloginOptional.get();
            }
        }

        throw new AuthenticationCredentialsNotFoundException("Error retrieving currently Authenticated user");
    }

    /**
     * Retrieves the user entity from the database based on a provided Authentication object.
     * <p>
     * Unlike the no-argument variant, this method returns {@code null} gracefully
     * rather than throwing an authentication exception if the provided authentication
     * is missing, unauthenticated, or belongs to the guest ("anonymousUser").
     *
     * @param authentication the Spring Security {@link Authentication} object to inspect.
     * @return the {@link MT_Userlogin} entity if found, or {@code null} if the user
     * is anonymous, not authenticated, or missing from the database.
     * @throws RuntimeException if an unexpected error occurs during the database query.
     */
    public MT_Userlogin getUserloginFromAuthentication(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        try {
            // .orElse(null) automatically does the isPresent() check for you!
            return mtUserloginRepository.findByUserId(authentication.getName()).orElse(null);
        } catch (Exception ex) {
            throw new RuntimeException("Error: Couldn't get currently authenticated user", ex);
        }
    }

    @Transactional(readOnly = true)
    public String getUserpasswordByUsercode(@NotNull @NotBlank String usercode) {
        try {
            return mtUserloginRepository.getUserpasswordByUsercode(usercode.trim());
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving userpassword by usercode " + usercode, ex);
        }
    }

    /*
     * This method checks MT_Userlogin existence by usercode
     * @params userocde Of MT_Userlogin for comparison
     * @returns Boolean specifying existence
     * @throws DataAccessResourceFailureException for db access errors
     */
    @Transactional(readOnly = true)
    public Boolean existsByUsercode(@NotNull @NotBlank String usercode) {
        try {
            return mtUserloginRepository.existsById(usercode.trim());
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error checking MT_Userlogin existence by usercode " + usercode, ex);
        }
    }

    public MT_Userlogin findByUserId(String userid) {
        Optional<MT_Userlogin> mtUserlogin = mtUserloginRepository.findByUserId(userid);
        return mtUserlogin.isPresent() ? mtUserlogin.get() : null;
    }

    public MT_Userlogin findByUsercode(@NotNull @NotBlank String usercode) {
        usercode = usercode.trim();

        try {
            Optional<MT_Userlogin> userOptional = mtUserloginRepository.findByUsercode(usercode);
            return userOptional.isPresent() ? userOptional.get() : null;
        } catch (Exception ex) {
            throw new RuntimeException("Error fetching MT_Userlogin entity");
        }
    }

    @Transactional(readOnly = true)
    public List<Object[]> getUserListByRoleAndOffice(@NotNull @NotBlank String urole, @NotNull @NotBlank String officecode) {
        try {
            return mtUserloginRepository.getUserListByRoleAndOffice(urole, officecode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving users list by role and office", ex);
        }
    }

    /*
     * This method retrievs Admin users list
     * @params urole Describing Admin user role
     * @officecode Of the users for matching
     * @throws DataAccessResourceFailureException for db access errors
     */
    @Transactional(readOnly = true)
    public List<Object[]> getadminUserList(@NotNull @NotBlank String urole, @NotNull @NotBlank String officecode) {
        try {
            return mtUserloginRepository.getAdminUserList(urole, officecode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving Admin users list", ex);
        }
    }

    /*
     * This method retrieving users list by role
     * @params urole User role to perform matching
     * @returns List of matched users
     * @throws DataAccessResourceFailureException for db access errors
     */
    @Transactional(readOnly = true)
    public List<Object[]> getUserListByRole(@NotNull @NotBlank String urole) {
        try {
            return mtUserloginRepository.getUserListByRole(urole);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving users list by role", ex);
        }
    }

    @Transactional(readOnly = true)
    public List<MT_Userlogin> getMT_UsersByRole(@NotNull @NotBlank String urole) {
        try {
            return mtUserloginRepository.findByRole_RoleCode(urole);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving MT_users list by role", ex);
        }
    }

    @Transactional(readOnly = true)
    public List<Object[]> getOfficeUserForCoordinator(@NotNull @NotBlank String officecode) {
        try {
            return mtUserloginRepository.getOfficeUserForCoordinator(officecode.trim());
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving MT_Userlogin list by officecode " + officecode, ex);
        }
    }

    /*
     * This method checks MT_Userlogin existence by userid or usercode or emailid or usermobile
     * @params user The MT_Userlogin instance for comparison
     * @returns Boolean specifying existence
     * @throws DataAccessResourceFailureException for db access errors
     */
    @Transactional(readOnly = true)
    public Boolean checkUserExists(@NotNull MT_Userlogin user) {
        try {
            return mtUserloginRepository.existsByUseridUsercodeEmailidUsermobile(
                user.getUserid(), user.getUsercode(), user.getEmailid(), user.getUsermobile());
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error checking MT_Userlogin existence by userid or usercode or emailid or usermobile", ex);
        }
    }

    public Boolean checkUserExists(@NotNull @NotBlank String emailId, String usercode) {
        try {
            String trimmedEmail = emailId != null ? emailId.trim() : "";
            if (usercode == null || usercode.isBlank()) {
                return mtUserloginRepository.checkIfEmailExistsIgnoreCase(trimmedEmail);
            } else {
                String trimmedUsercode = usercode.trim();
                return mtUserloginRepository.checkIfEmailExistsIgnoreCaseExcludingUsercode(trimmedEmail, trimmedUsercode);
            }
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error checking if user exists", ex);
        }
    }

    @Transactional(readOnly = false)
    public boolean toggleUserStatus(@NotNull @NotBlank String usercode) {
        try {
            int updatedRows = mtUserloginRepository.toggleUserStatus(usercode.trim());
            return updatedRows > 0;
        } catch (Exception e) {
            throw new RuntimeException("Error toggling user status for usercode: " + usercode, e);
        }
    }

    public Integer updateUserphotographByUsercode(String usercode, byte[] userphotograph) {
        return mtUserloginRepository.updateUserphotographByUsercode(usercode, userphotograph);
    }

    @Transactional
    public MT_Userlogin updateUserPassword(@NotNull MT_Userlogin currentUser, @NotBlank String newPassword) {
        currentUser.setUserpassword(getBcryptPassword(newPassword));
        currentUser.setUseBcrypt(true);

        if (!"P".equals(currentUser.getRole() != null ? currentUser.getRole().getRoleCode().toUpperCase() : "")) {
            currentUser.setIsmodified("Y");
        }

        return mtUserloginRepository.save(currentUser);
    }

    public boolean verifyPassword(@NotNull @NotBlank String userId, @NotNull @NotBlank String plainTextPassword) {
        if (!StringUtils.hasText(userId)) {
            throw new IllegalArgumentException("User ID cannot be empty for password verification.");
        }
        if (!StringUtils.hasText(plainTextPassword)) {
            throw new IllegalArgumentException("Password cannot be empty for verification.");
        }

        // Fetch User
        MT_Userlogin user = this.findByUserId(userId);
        if (user == null) {
            throw new EntityNotFoundException("User not found with ID: " + userId);
        }

        String storedPasswordHash = user.getUserpassword();
        boolean useBcrypt = user.getUseBcrypt();

        // Check if stored hash exists
        if (!StringUtils.hasText(storedPasswordHash)) {
            return false;
        }

        boolean matches;
        try {
            if (useBcrypt) {
                matches = passwordEncoder.matches(plainTextPassword, storedPasswordHash);
            } else {
                String hashedInputPassword = SHA256Util.getHash(plainTextPassword);
                if (hashedInputPassword == null) {
                    throw new RuntimeException("Failed to hash input password using SHA-256.");
                }
                matches = storedPasswordHash.equals(hashedInputPassword);
            }
        } catch (Exception e) {
            return false;
        }

        return matches;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public MT_Userlogin save(@NotNull MT_Userlogin mtUserlogin) {
        if (mtUserlogin.getUsercode() == null || mtUserlogin.getUsercode().isBlank())
            mtUserlogin.setUsercode(generateUsercode());

        try {
            return mtUserloginRepository.save(mtUserlogin);
        } catch (Exception e) {
            throw new RuntimeException("Error saving MT_Userlogin entity | Exception = " + e);
        }
    }

    /*
     * This method creates or updates MT_Userlogin and create user-process mappings
     * @params user The MT_Userlogin to create or update
     * @params processesList List of process code of the user
     * @returns The persisted MT_Userlogin
     * @throws NumberFormatException If processcode does not exist in db
     * @throws RuntimeException For persistence errors
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, readOnly = false)
    public MT_Userlogin saveUserDetailsAndProcessMappings(@NotNull MT_Userlogin user, List<String> processes) {
        // generate and set usercode for new users (Creation)
        if (user.getUsercode() == null || user.getUsercode().isBlank()) 
            user.setUsercode(generateUsercode());

        // save or update the user
        MT_Userlogin savedUser = null;
        try {
            savedUser = mtUserloginRepository.save(user);
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (savedUser == null)
                throw new RuntimeException("Error saving MT_Userlogin entity");
        }

        // Deleting existing user-process mappings from mt_userprocesses
        // if user is not modified i.e., Creation, then no mappings exists
        if (user.getIsmodified() == "Y") {
            try {
                mProcessesRepository.removeUserProcessEntry(user.getUsercode());
            } catch (Exception ex) {
                throw new RuntimeException("Error removing M_UserProcesses mappings", ex);
            }
        }

        // Creating mt_userprocesses entry for user-process mapping
        if ((processes == null || processes.size() == 0)) {
            // [Old] Principal director (Z) can be created without a single process
            if (user.getUserrole().equals("Z"))
                return savedUser;
            else
                throw new RuntimeException("Error creating M_UserProcesses mappings. Processes cannot be null for role A & U");
        }

        for (String process : processes) {
            try {
                mProcessesRepository.createUserProcessesEntry(user.getUsercode(), Integer.parseInt(process));
            } catch (NumberFormatException ex) {
                throw new NumberFormatException("Invalid Process code");
            } catch (Exception ex) {
                throw new RuntimeException("Error creating M_UserProcesses entity", ex);
            }
        }

        return savedUser;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class, readOnly = false)
    public MT_Userlogin saveUserDetails(@NotNull MT_Userlogin user) {
        // 1. Generate usercode for new users if missing
        if (user.getUsercode() == null || user.getUsercode().isBlank())
            user.setUsercode(generateUsercode());

        // 2. Save or update the user entity only
        MT_Userlogin savedUser = null;
        try {
            savedUser = mtUserloginRepository.save(user);
        } catch (Exception ex) {
            dataPersistenceLogger.error("Error saving MT_Userlogin: {}", ex.getMessage());
        } finally {
            if (savedUser == null)
                throw new RuntimeException("Error saving MT_Userlogin entity");
        }

        // 3. Return saved user.
        // We NO LONGER insert into nerie.mt_userprocesses.
        // Access is now determined dynamically via nerie.mt_role_process.
        return savedUser;
    }

    @Transactional
    public MT_Userlogin saveUserloginProfileDetails(@NotNull MT_Userlogin mtUserlogin) {
        MT_Userlogin savedUser = mtUserloginRepository.save(mtUserlogin);

        return savedUser;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String registerUserloginParticipant(@NotNull MT_Userlogin mtUserlogin) {
        if (mtUserlogin.getEmailid() == null || mtUserlogin.getUserpassword() == null)
            return "1"; // Invalid input

        if (checkUserExists(mtUserlogin.getEmailid(), null)) return "4"; // Email already exists

        // Set role
        MT_UserloginRole participantRole = roleRepository.findByRoleCode("P")
                .orElseThrow(() -> new RuntimeException("Role 'P' not found"));

        // Prepare user login entity
        mtUserlogin.setUserid(mtUserlogin.getEmailid().trim());
        mtUserlogin.setUserpassword(passwordEncoder.encode(mtUserlogin.getUserpassword().trim()));
        mtUserlogin.setRole(participantRole);
        mtUserlogin.setUserrole("P");
        mtUserlogin.setEnabled(1);
        mtUserlogin.setIsmodified("Y");
        mtUserlogin.setUserdescription("Participant");
        mtUserlogin.setIsfaculty("0");
        mtUserlogin.setUseBcrypt(true);

        // Generate usercode
        if (mtUserlogin.getUsercode() == null || mtUserlogin.getUsercode().trim().isEmpty()) {
            int newCode = mtUserloginRepository.findMaxUsercodeAsInt().orElse(0) + 1;
            mtUserlogin.setUsercode(String.valueOf(newCode));
        }

        // Save and return usercode
        return mtUserloginRepository.save(mtUserlogin).getUsercode();
    }

    @Transactional
    public MT_Userlogin updateUserloginParticipant(String usercode, String newEmailId, String newUsername, String newMobileNumber) {

        // Validate inputs
        if (!StringUtils.hasText(usercode)) {
            throw new IllegalArgumentException("Usercode cannot be empty for updating login details.");
        }
        if (!StringUtils.hasText(newEmailId) || !newEmailId.matches("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {
            throw new IllegalArgumentException("Invalid Email ID format provided.");
        }
        if (!StringUtils.hasText(newUsername)) { throw new IllegalArgumentException("Username cannot be empty.");
        }
        if (!StringUtils.hasText(newMobileNumber) || !newMobileNumber.matches("\\d{10}")) {
            throw new IllegalArgumentException("Mobile number must be 10 digits.");
        }

        String trimmedEmail = newEmailId.trim();
        String trimmedUsername = newUsername.trim();
        String trimmedMobile = newMobileNumber.trim();

        // Check if the new email is already used by ANOTHER user
        if (checkUserExists(trimmedEmail, usercode)) {
            throw new IllegalArgumentException("The email address '" + trimmedEmail + "' is already registered to another account. Please use a different email.");
        }

        // Fetch the existing user entity from the database
        MT_Userlogin existingLogin = mtUserloginRepository.findByUsercode(usercode)
                .orElseThrow(() -> {
                    return new EntityNotFoundException("Could not find user profile to update (User code: " + usercode + ").");
                });

        // Update the fields
        existingLogin.setUserid(trimmedEmail);
        if (existingLogin.getEmailid() != null) {
            existingLogin.setEmailid(trimmedEmail);
        }
        existingLogin.setUsername(trimmedUsername);
        existingLogin.setUsermobile(trimmedMobile);
        existingLogin.setIsmodified("N");

        // Save the updated entity
        try {
            MT_Userlogin savedParticipant = mtUserloginRepository.save(existingLogin);
            return savedParticipant;
        } catch (Exception e) {
            throw new RuntimeException("Failed to save updated user login details due to a database error.", e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public MT_Userlogin saveUserDetailsForStudent(@NotNull @NotBlank String userid, @NotNull @NotBlank String username, @NotNull @NotBlank String password, @NotNull M_Offices office) {
        MT_Userlogin newStudentUser = new MT_Userlogin();

        try {
            newStudentUser.setUsercode(generateUsercode());
        } catch (RuntimeException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }

        newStudentUser.setUserid(userid);
        newStudentUser.setUsername(username);
        newStudentUser.setIsmodified("Y");
        newStudentUser.setUserdescription("Student");
        newStudentUser.setEnabled((short) 1);
        newStudentUser.setUseBcrypt(true);
        newStudentUser.setMoffices(office);

        newStudentUser.setUserrole("T");

        try {
            MT_UserloginRole newStudentUserRole = mtUserloginRoleService.findByRoleCode("T");
            if (newStudentUserRole != null)
                newStudentUser.setRole(newStudentUserRole);
            else
                throw new EntityNotFoundException("Role 'T' not found for student user.");
        } catch (Exception ex) {
            throw new RuntimeException("Failed to set role for new student user: " + ex.getMessage(), ex);
        }

        newStudentUser.setIsfaculty("0");
        newStudentUser.setUserpassword(getBcryptPassword(password));

        try {
            return mtUserloginRepository.save(newStudentUser);
        } catch (Exception e) {
            throw new RuntimeException("Failed to save user details for student.", e);
        }
    }

    public String getBcryptPassword(String newPassword) {
        return passwordEncoder.encode(newPassword.trim());
    }

    @Transactional(readOnly = true)
    private String generateUsercode() {
        try {
            Integer lastUsercodeUsed = mtUserloginRepository.findLastUsercodeUsed();
            return lastUsercodeUsed == null ? "1" : String.valueOf(lastUsercodeUsed + 1);
        } catch (NumberFormatException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public List<Object[]> getFacultyCandidatesByUser(@NotNull @NotBlank String usercode, @NotNull @NotBlank String officecode) {
        try {
            return mtUserloginRepository.findFacultyCandidatesByUsercode(usercode, officecode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException(String.format("Error retrieving getFacultyCandidatesByUser with usercode=%s, officecode=%s", usercode, officecode), ex);
        }
    }

    public List<Object[]> getFacultyCandidates(@NotBlank String officecode) {
        try {
            return mtUserloginRepository.findFacultyCandidates(officecode);
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving getFacultyCandidates with office code: " + officecode, ex);
        }
    }

    public boolean checkParticipantEmailProfileUpdate(String emailid, String usercode) {
        if (emailid == null || usercode == null) return false;

        MT_Userlogin result = mtUserloginRepository.checkEmailExistsForUpdate(emailid.toUpperCase(), usercode);
        return result != null;
    }

    @Transactional
    public boolean updateParticipantProfile(String emailid, String username, String usermobile, String designationcode, T_Participants tparticipant) {
        try {
            int updated = mtUserloginRepository.updateParticipantUserProfile(emailid, username, usermobile, designationcode, tparticipant.getUsercode());

            if (updated > 0) {
                tParticipantsRepository.save(tparticipant);  // save or update participant
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Transactional(readOnly = true)
    public String getCoordinatorsCount(@NotNull @NotBlank String officecode) {
        try {
            return String.valueOf(mtUserloginRepository.getCoordinatorsCount(officecode.trim()));
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error retrieving coordinators count for office " + officecode, ex);
        }
    }

    public List<MT_Userlogin> getParticipantsForPhase(@NotNull M_Phases phase) {
        try {
            return mtUserloginRepository.findParticipantsByPhase(phase.getPhaseid());
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error fetching participant for phase: " + phase, ex);
        }
    }

    @Transactional(readOnly = true)
    public Boolean getIsUserEnabledByUserid(@NotNull @NotBlank String userid) {
        try {
            return mtUserloginRepository.isUserEnabled(userid.trim());
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error checking account enabled by userid " + userid, ex);
        }
    }

    @Transactional(readOnly = true)
    public Boolean getIsUseBcryptByUsercode(@NotNull @NotBlank String usercode) {
        try {
            return mtUserloginRepository.isUserBcrypt(usercode.trim());
        } catch (Exception ex) {
            throw new DataAccessResourceFailureException("Error checking if bcrypt is used by usercode " + usercode, ex);
        }
    }
}
