package com.example.reservebite.service;

import com.example.reservebite.entity.Role;
import com.example.reservebite.repository.RoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class RoleService {
    private final RoleRepository roleRepository;

    public RoleService(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    //get all roles
    public List<Role> getAllRoles(){
        return roleRepository.findAll();
    }

    //get one role by ID
    public Role getRoleById(Long id){
        return roleRepository.findById(id).orElseThrow(() -> new NoSuchElementException("No such Role with this ID"));
    }

    //Save role
    public void saveRole(Role role){
        roleRepository.save(role);
    }

    //Edit role
    public void editRole(Long id, Role role){
        Role roleById = getRoleById(id);
        roleById.setName(role.getName());
        roleById.setActive(role.isActive());
        saveRole(roleById);
    }

    //Delete role
    public void deleteRole(Long id){
        roleRepository.deleteById(id);
    }


}
