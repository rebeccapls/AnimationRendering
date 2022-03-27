package com.rebecca.controller

import com.rebecca.helper.NpcDrawingHelper
import com.rebecca.rs2.npc.impl.NpcService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping


/**
 *  The observer class of any / requests
 */
@Controller
class IndexController {

    @Autowired
    lateinit var npc: NpcDrawingHelper

    /**
     *  Observes and populates the index.html template
     *
     *  @param model holder of specified attributes inserted into the template
     *  @return returns the .html file
     */
    @GetMapping("/")
    fun index(model: Model): String? {
        return "index"
    }
}
