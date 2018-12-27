import { IActivity } from 'app/shared/model//activity.model';

export interface IExercise {
    id?: number;
    minutes?: number;
    activities?: IActivity[];
}

export class Exercise implements IExercise {
    constructor(public id?: number, public minutes?: number, public activities?: IActivity[]) {}
}
