import { NgModule, CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';

import { FitnessTracker2ActivityModule } from './activity/activity.module';
import { FitnessTracker2ExerciseModule } from './exercise/exercise.module';
import { FitnessTracker2GoalModule } from './goal/goal.module';
/* jhipster-needle-add-entity-module-import - JHipster will add entity modules imports here */

@NgModule({
    // prettier-ignore
    imports: [
        FitnessTracker2ActivityModule,
        FitnessTracker2ExerciseModule,
        FitnessTracker2GoalModule,
        /* jhipster-needle-add-entity-module - JHipster will add entity modules here */
    ],
    declarations: [],
    entryComponents: [],
    providers: [],
    schemas: [CUSTOM_ELEMENTS_SCHEMA]
})
export class FitnessTracker2EntityModule {}
