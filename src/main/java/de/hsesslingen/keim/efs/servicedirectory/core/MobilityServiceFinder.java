/*
 * MIT License
 * 
 * Copyright (c) 2020 Hochschule Esslingen
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE. 
 */
package de.hsesslingen.keim.efs.servicedirectory.core;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;

import de.hsesslingen.keim.efs.mobility.service.MobilityService;
import de.hsesslingen.keim.efs.mobility.service.MobilityService.API;
import de.hsesslingen.keim.efs.mobility.service.Mode;
import static java.util.Collections.disjoint;
import org.springframework.stereotype.Service;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Utility class for finding registered services.
 *
 * @author k.sivarasah 12 Sep 2019
 */
@Service
public class MobilityServiceFinder {

    @Autowired
    private MobilityServiceRegistry registry;

    /**
     * Searches for services with consideration of the provided (optional)
     * parameters.
     *
     * @param modes Set of {@link Mode}
     * @param serviceIds Set of service ids to filter by.
     * @param apis Set of APIs of which a service must support all.
     * @param excludeInactive Excludes inactive mobility provider services from
     * the result list.
     *
     * @return List of {@link MobilityService}
     */
    public List<MobilityService> search(
            Set<Mode> modes,
            Set<API> apis,
            boolean excludeInactive,
            Set<String> serviceIds
    ) {
        return registry.streamAll(excludeInactive)
                .filter(service
                        -> (isEmpty(modes) || !disjoint(modes, service.getModes()))
                && (isEmpty(serviceIds) || serviceIds.stream().anyMatch(service.getId()::equalsIgnoreCase))
                && (isEmpty(apis) || service.getApis().containsAll(apis))
                )
                .collect(Collectors.toList());
    }

    /**
     * Search for currently available services that provide the wished
     * {@link MobilityType}s
     *
     * @param modes Set of {@link Mode}
     * @return List of {@link Mode}
     */
    public List<MobilityService> searchByModes(Set<Mode> modes) {
        return search(modes, null, true, null);
    }
}
