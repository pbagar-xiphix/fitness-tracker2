import { IExercise } from 'app/shared/model//exercise.model';

export interface IActivity {
    id?: number;
    desc?: string;
    exercises?: IExercise[];
}

export class Activity implements IActivity {
    constructor(public id?: number, public desc?: string, public exercises?: IExercise[]) {}
}
