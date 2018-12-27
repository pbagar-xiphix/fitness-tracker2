import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, RouterStateSnapshot, Routes } from '@angular/router';
import { UserRouteAccessService } from 'app/core';
import { Observable, of } from 'rxjs';
import { filter, map } from 'rxjs/operators';
import { Exercise } from 'app/shared/model/exercise.model';
import { ExerciseService } from './exercise.service';
import { ExerciseComponent } from './exercise.component';
import { ExerciseDetailComponent } from './exercise-detail.component';
import { ExerciseUpdateComponent } from './exercise-update.component';
import { ExerciseDeletePopupComponent } from './exercise-delete-dialog.component';
import { IExercise } from 'app/shared/model/exercise.model';

@Injectable({ providedIn: 'root' })
export class ExerciseResolve implements Resolve<IExercise> {
    constructor(private service: ExerciseService) {}

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Exercise> {
        const id = route.params['id'] ? route.params['id'] : null;
        if (id) {
            return this.service.find(id).pipe(
                filter((response: HttpResponse<Exercise>) => response.ok),
                map((exercise: HttpResponse<Exercise>) => exercise.body)
            );
        }
        return of(new Exercise());
    }
}

export const exerciseRoute: Routes = [
    {
        path: 'exercise',
        component: ExerciseComponent,
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'fitnessTracker2App.exercise.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'exercise/:id/view',
        component: ExerciseDetailComponent,
        resolve: {
            exercise: ExerciseResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'fitnessTracker2App.exercise.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'exercise/new',
        component: ExerciseUpdateComponent,
        resolve: {
            exercise: ExerciseResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'fitnessTracker2App.exercise.home.title'
        },
        canActivate: [UserRouteAccessService]
    },
    {
        path: 'exercise/:id/edit',
        component: ExerciseUpdateComponent,
        resolve: {
            exercise: ExerciseResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'fitnessTracker2App.exercise.home.title'
        },
        canActivate: [UserRouteAccessService]
    }
];

export const exercisePopupRoute: Routes = [
    {
        path: 'exercise/:id/delete',
        component: ExerciseDeletePopupComponent,
        resolve: {
            exercise: ExerciseResolve
        },
        data: {
            authorities: ['ROLE_USER'],
            pageTitle: 'fitnessTracker2App.exercise.home.title'
        },
        canActivate: [UserRouteAccessService],
        outlet: 'popup'
    }
];
