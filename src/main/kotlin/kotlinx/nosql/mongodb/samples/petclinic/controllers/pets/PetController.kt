package kotlinx.nosql.mongodb.samples.petclinic.controllers.owners

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import kotlinx.nosql.mongodb.*
import kotlinx.nosql.*
import org.springframework.ui.Model
import kotlinx.nosql.mongodb.samples.petclinic.data.Owners
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestMethod
import kotlinx.nosql.mongodb.samples.petclinic.data.Pets
import kotlinx.nosql.mongodb.samples.petclinic.data.PetTypes
import kotlinx.nosql.mongodb.samples.petclinic.data.Pet
import org.joda.time.LocalDate
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.beans.factory.annotation.Autowired

Controller RequestMapping(value = array("/pets"))
class PetController [Autowired] (val db: MongoDB) {
    RequestMapping(value = array("/add"), method = array(RequestMethod.GET))
    public fun add(RequestParam("ownerId") ownerIdParam: String, model: Model): String {
        db.withSession {
            val owner = Owners.get(Id(ownerIdParam))
            model.addAttribute("owner", owner)
            val petTypes = PetTypes.findAll { name.equal(name) }.toList()
            model.addAttribute("petTypes", petTypes)
        }
        return "pets/add"
    }

    RequestMapping(value = array("/edit"), method = array(RequestMethod.GET))
    public fun edit(RequestParam("id") idParam: String, model: Model): String {
        db.withSession {
            val pet = Pets.get(Id(idParam))
            model.addAttribute("pet", pet)
            val owner = Owners.get(pet.ownerId)
            model.addAttribute("owner", owner)
            val petTypes = PetTypes.findAll { name.equal(name) }.toList()
            model.addAttribute("petTypes", petTypes)
        }
        return "pets/edit"
    }

    RequestMapping(value = array("/add"), method = array(RequestMethod.POST))
    public fun add(RequestParam("ownerId") ownerIdParam: String,
                   RequestParam("name") nameParam: String,
                   RequestParam("birthDate") DateTimeFormat(pattern = "dd/MM/yyyy") birthDateParam: LocalDate,
                   RequestParam("typeId") typeIdParam: String): String {
        db.withSession {
            Pets.insert(Pet(nameParam, birthDateParam, Id(typeIdParam), Id(ownerIdParam)))
        }
        return "redirect:/owners/view?id=${ownerIdParam}"
    }

    RequestMapping(value = array("/edit"), method = array(RequestMethod.POST))
    public fun edit(RequestParam("id") idParam: String,
                    RequestParam("name") nameParam: String,
                    RequestParam("birthDate") DateTimeFormat(pattern = "dd/MM/yyyy") birthDateParam: LocalDate,
                    RequestParam("typeId") typeIdParam: String): String {
        var oId: Id<String, Owners>? = null
        db.withSession {
            oId = Pets.select { ownerId }.get(Id(idParam))
            Pets.select { name + birthDate + typeId }.find(Id(idParam)).set(nameParam, birthDateParam, Id(typeIdParam))
        }
        return "redirect:/owners/view?id=${oId!!}"
    }
}